package com.faiop.core.service.impl;

import com.faiop.core.mapper.*;
import com.faiop.core.pojo.*;
import com.faiop.core.service.StatisticsService;
import com.faiop.core.util.DateUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author RM
 */
@Service
public class StatisticsServiceImpl implements StatisticsService  {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    DeptMapper deptMapper;
    @Autowired
    SalaryHistoryMapper salaryHistoryMapper;
    @Autowired
    ResourceMapper resourceMapper;
    @Autowired
    ResourceHistoryMapper resourceHistoryMapper;

    @Override
    public Map<String, Object> countEmps(String name, String state, String dept, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //设置查询条件
        List<Long> userIds = getAlldeptsAndEmps().get(dept == null ? "" : dept);
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != userIds){
            criteria.andIn("id",userIds.size() == 0 ? Arrays.asList(new Long[] {-1L}) : userIds);
        }
        if(null != state && !"".equals(state)){
            criteria.andEqualTo("state",state);
        }
        if(null != name && !"".equals(name)){
            criteria.andLike("name","%" + name + "%");
        }
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Page<User> page = PageHelper.startPage(pageno,6,true);
        List<User> users = userMapper.selectByExample(example);
        for(User u : users){
            u.setRole(roleMapper.selectByPrimaryKey(u.getRoleId()));
        }
        //打包回传数据
        map.put("dataList",users);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("name",name);
        map.put("dept",dept);
        map.put("state",state);
        return map;
    }

    @Override
    public Map<String, Object> countResourceHistory(String resourceName, String type, String beginTime, String endTime, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //设置查询条件
        Example example = new Example(Resource.class);
        example.createCriteria().andLike("name","%"+resourceName+"%");
        List<Long> resourceIds = resourceMapper.selectByExample(example).stream().map(Resource::getId).collect(Collectors.toList());
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        example = new Example(ResourceHistory.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != type && !"".equals(type)){
            criteria.andEqualTo("type",type);
        }
        criteria.andIn("resourceId",resourceIds.size() != 0 ? resourceIds : Arrays.asList(new Long[]{-1L}));
        if(null != beginTime && !"".equals(beginTime)){
            criteria.andGreaterThanOrEqualTo("updateTime",beginTime);
        }
        if(null != beginTime && !"".equals(beginTime)){
            criteria.andLessThanOrEqualTo("updateTime",beginTime);
        }
        Page<ResourceHistory> page = PageHelper.startPage(pageno,6,true);
        List<ResourceHistory> resourceHistories = resourceHistoryMapper.selectByExample(example);
        for(ResourceHistory rh : resourceHistories){
            //为每个资源审核对象补充其依赖对象数据，达到联合查询的目的
            rh.setResource(resourceMapper.selectByPrimaryKey(rh.getResourceId()));
            rh.setOriginate(userMapper.selectByPrimaryKey(rh.getOriginateId()));
            rh.setApproval(rh.getApprovalId() == null ? null : userMapper.selectByPrimaryKey(rh.getApprovalId()));
            rh.getOriginate().setRole(roleMapper.selectByPrimaryKey(rh.getOriginate().getRoleId()));
        }
        //打包回传数据
        map.put("dataList",resourceHistories);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("resourceName",resourceName);
        map.put("type",type);
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        return map;
    }


    @Override
    public Map<String, Object> countEmpSalary(String id, String beginTime, String endTime) {
        Map<String, Object> resultMap = new HashMap<>();
        //申明查询时间包含的月份集
        List<String> months = new ArrayList<>();
        //申明员工各月的薪资集
        List<BigDecimal> empSalary = new ArrayList<>();
        Example example = new Example(SalaryHistory.class);
        Example.Criteria criteria = example.createCriteria();
        //设置员工id
        criteria.andEqualTo("userId", id);
        //设置查询时间范围
        criteria.andGreaterThanOrEqualTo("updateTime",beginTime).andLessThanOrEqualTo("updateTime",endTime);
        List<SalaryHistory> salaryHistories = salaryHistoryMapper.selectByExample(example);
        for (SalaryHistory s:salaryHistories) {
            //将薪资总和添加到集合里
            empSalary.add(s.getTotal());
            //解析时间数据
            String year = DateUtil.parseDateToStr(s.getUpdateTime(),"yyyy");
            String month = DateUtil.parseDateToStr(s.getUpdateTime(),"MM");
            //如果月份为1，那么薪资记录即为去年的12月份，否则月份减一即为实际月份
            String time = Integer.valueOf(month) == 1 ?
                    (Integer.valueOf(year)-1)+"年12月" : year+"年"+(Integer.valueOf(month)-1)+"月";
            //将月份添加到集合里
            months.add(time);
        }
        //打包数据集合
        resultMap.put("dataList",months);
        resultMap.put("options",empSalary);
        return resultMap;
    }

    @Override
    public List<Integer> getYears() {
        List<Integer> lists = new ArrayList<>();
        String minYear = salaryHistoryMapper.selectByTime("asc");
        String maxYear = salaryHistoryMapper.selectByTime("desc");
        for (int year = Integer.valueOf(minYear.substring(0,minYear.indexOf("-")));year <= Integer.valueOf(minYear.substring(0,maxYear.indexOf("-")));year++){
            lists.add(year);
        }
        return lists;
    }

    @Override
    public Map<String, Object> countDelptSalaryByYear(String year) {
        Map<String, Object> resultMap = new HashMap<>();
        String minyear = year;
        String maxyear = year;
        //解析查询范围
        //给定x年，那么范围即为(x-01-03,(x+1)-01-03],因为一年的薪资记录是从02-03到第二年的01-03
        if(null != year && !"".equals(year)){
            minyear = year + "-01-03";
            maxyear = (Integer.valueOf(year)+1)+"-01-03";
        }
        //申明部门名称集
        List<String> deptNames = new ArrayList<>();
        //申明部门薪资集
        List<BigDecimal> deptTotalSalarys = new ArrayList<>();
        //迭代部门与员工map集合
        Iterator<Map.Entry<String, List<Long>>> iterator = getAlldeptsAndEmps().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, List<Long>> next = iterator.next();
            Example example = new Example(SalaryHistory.class);
            Example.Criteria criteria = example.createCriteria();
            //获取当前部门的员工id集，如果没有员工则查询id = -1的记录，即无结果返回
            criteria.andIn("userId",next.getValue().size() != 0 ? next.getValue() : Arrays.asList(new Long[] {-1L}));
            if(null != year){
                //设置查询年份范围
                criteria.andGreaterThan("updateTime",minyear).andLessThanOrEqualTo("updateTime",maxyear);
            }
            BigDecimal reduce = salaryHistoryMapper.selectByExample(example).stream().map(SalaryHistory::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            deptNames.add(next.getKey());
            deptTotalSalarys.add(reduce);
        }
        //打包数据集合
        resultMap.put("dataList",deptNames);
        resultMap.put("options",deptTotalSalarys);
        return resultMap;
    }

    @Override
    public Map<String, Object> countDelptSalaryByQuarter(String year, String quarter) {
        Map<String, Object> resultMap = new HashMap<>();
        String maxTime = null;
        String minTime = null;
        //解析查询范围
        //需要注意的是：第四季度的查询范围是  (x-10-03,(x+1)-01-03],其他查询范围为 (x-(y-1)*3+1-03,x-(y-1)*3+4-03]
        if(null != year && !"".equals(year) && null != quarter && !"".equals(quarter)){
            maxTime = ((Integer.valueOf(quarter)-1)*3 + 4) != 13 ? year + "-0" + ((Integer.valueOf(quarter)-1)*3 + 4) + "-03" : (Integer.valueOf(year)+1) + "-01-03";
            minTime = year + "-0" + ((Integer.valueOf(quarter)-1)*3 + 1) + "-03";
        }
        //申明部门名称集
        List<String> deptNames = new ArrayList<>();
        //申明部门薪资集
        List<BigDecimal> deptTotalSalarys = new ArrayList<>();
        //迭代部门与员工map集合
        Iterator<Map.Entry<String, List<Long>>> iterator = getAlldeptsAndEmps().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, List<Long>> next = iterator.next();
            Example example = new Example(SalaryHistory.class);
            Example.Criteria criteria = example.createCriteria();
            //获取当前部门的员工id集，如果没有员工则查询id = -1的记录，即无结果返回
            criteria.andIn("userId",next.getValue().size() != 0 ? next.getValue() : Arrays.asList(new Long[] {-1L}));
            if(null != maxTime && null != minTime){
                //设置查询季度范围
                criteria.andGreaterThan("updateTime",minTime).andLessThanOrEqualTo("updateTime",maxTime);
            }
            BigDecimal reduce = salaryHistoryMapper.selectByExample(example).stream().map(SalaryHistory::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            deptNames.add(next.getKey());
            deptTotalSalarys.add(reduce);
        }
        //打包数据集合
        resultMap.put("dataList",deptNames);
        resultMap.put("options",deptTotalSalarys);
        return resultMap;
    }

    @Override
    public Map<String, Object> countDelptSalaryByMonth(String year, String month) {
        Map<String, Object> resultMap = new HashMap<>();
        String time = null;
        //解析查询范围
        //需要注意的是：普通查询范围是 x-(y+1)-03,而12月份是 (x+1)-01-03
        if(null != year && !"".equals(year) && null != month && !"".equals(month)){
            time = Integer.valueOf(month) != 12 ? year + "-0" + (Integer.valueOf(month)+1) + "-03" : (Integer.valueOf(year)+1) + "-01-03";
        }
        //申明部门名称集
        List<String> deptNames = new ArrayList<>();
        //申明部门薪资集
        List<BigDecimal> deptTotalSalarys = new ArrayList<>();
        //迭代部门与员工map集合
        Iterator<Map.Entry<String, List<Long>>> iterator = getAlldeptsAndEmps().entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, List<Long>> next = iterator.next();
            Example example = new Example(SalaryHistory.class);
            Example.Criteria criteria = example.createCriteria();
            //获取当前部门的员工id集，如果没有员工则查询id = -1的记录，即无结果返回
            criteria.andIn("userId",next.getValue().size() != 0 ? next.getValue() : Arrays.asList(new Long[] {-1L}));
            if(null != time){
                //查询指定的月份
                criteria.andLike("updateTime","%" + time + "%");
            }
            BigDecimal reduce = salaryHistoryMapper.selectByExample(example).stream().map(SalaryHistory::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            deptNames.add(next.getKey());
            deptTotalSalarys.add(reduce);
        }
        //打包数据集合
        resultMap.put("dataList",deptNames);
        resultMap.put("options",deptTotalSalarys);
        return resultMap;
    }

    @Override
    public Map<String, List<Long>> getAlldeptsAndEmps(){
        Map<String,List<Long>> map = new HashMap<>();
        List<Dept> depts = deptMapper.selectAll();
        for (Dept dept: depts) {
            List<Long> roleIds = roleMapper.selectSimilarRole(dept.getId());
            Example example = new Example(User.class);
            example.createCriteria().andIn("roleId",roleIds);
            List<Long> userIds = userMapper.selectByExample(example).stream().map(User::getId).collect(Collectors.toList());
            map.put(dept.getName(),userIds);
        }
        return map;
    }
}
