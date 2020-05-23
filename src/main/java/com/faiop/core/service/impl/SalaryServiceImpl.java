package com.faiop.core.service.impl;

import com.faiop.core.mapper.RoleMapper;
import com.faiop.core.mapper.SalaryHistoryMapper;
import com.faiop.core.mapper.SalaryMapper;
import com.faiop.core.mapper.UserMapper;
import com.faiop.core.pojo.Resource;
import com.faiop.core.pojo.Salary;
import com.faiop.core.pojo.SalaryHistory;
import com.faiop.core.pojo.User;
import com.faiop.core.service.SalaryService;
import com.faiop.core.util.DateUtil;
import com.faiop.core.util.UserContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author RM
 */
@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    SalaryMapper salaryMapper;

    @Override
    public Map<String, Object> getHistorySalarys(String time, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Example example = new Example(SalaryHistory.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != time && !"".equals(time)){
            criteria.andGreaterThan("time",time);
        }
        criteria.andEqualTo("userId",UserContext.getCurrentUser().getId());
        Page<SalaryHistory> page = PageHelper.startPage(pageno,5,true);
        List<SalaryHistory> salaryHistories = salaryHistoryMapper.selectByExample(example);
        //打包回传数据
        map.put("dataList",salaryHistories);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("time",time);
        return map;
    }

    @Override
    public Map<String, Object> getNowSalary() {
        Map<String ,Object> map = new HashMap<>();
        //获取当前需要查询的事哪月的薪资单
        int needYear = Calendar.getInstance().get(Calendar.YEAR);
        int needMonth = Calendar.getInstance().get(Calendar.MONTH);
        SalaryHistory salaryHistory = salaryHistoryMapper.selectRecentSalary(UserContext.getCurrentUser().getId());
        //如果没有数据，就告诉用户没有薪资单
        if(null != salaryHistory){
            //解析出最近的历史薪资的更新日期
            Date recentTime = salaryHistory.getUpdateTime();
            String recentYear = DateUtil.parseDateToStr(recentTime,"yyyy");
            String recentMonth = DateUtil.parseDateToStr(recentTime,"MM");
            String recentDate = DateUtil.parseDateToStr(recentTime,"dd");
            //如果年份不相等，则告诉用户没有薪资单
            if(needYear == Integer.valueOf(recentYear)){
                //如果当前月为x,那么薪资单的更新日期在（x-1月4日---x月3日）则符合要求，将薪资单发送到页面
                if(needMonth < Integer.valueOf(recentMonth)){
                    map.put("salary",salaryHistory);
//                    System.out.println("salaryHistory = " + salaryHistory);
                    map.put("isShow",true);
                }
                else {
                    map.put("salary",new Salary());
                    map.put("isShow",false);
                }
            }
            else {
                map.put("salary",new Salary());
                map.put("isShow",false);
            }
        }
        else {
            map.put("salary",new Salary());
            map.put("isShow",false);
        }
        return map;
    }

    @Autowired
    SalaryHistoryMapper salaryHistoryMapper;

//    /**
//     * 主管手动下发薪资单
//     * @return
//     */
//    @Override
//    @Transactional
//    public String issueSalarys() {
//        //先更新当月薪资表中的更新时间
//        int timeResult = salaryMapper.updateAllTime(new Date());
//        if(timeResult < 1){
//            return "201";
//        }
//        //把当月薪资数据转移到历史记录表中
//        int MDResult = salaryHistoryMapper.insertMonthData();
//        if(MDResult < 1){
//            return "201";
//        }
//        //把薪资表的数据更新到基础状态
//        int TBDResult = salaryMapper.updateToBaseData();
//        if(TBDResult < 1){
//            return "201";
//        }
//        return "200";
//    }

    /**
     * 通过定时任务下发薪资单
     * @return
     */
    @Transactional
    @Scheduled(cron = "0 0 0 3 * ?")
//    @Scheduled(cron = "0 0 */1 * * ?")
    public String issueAllSalarys() {
        //先更新当月薪资表中的更新时间
        int timeResult = salaryMapper.updateAllTime(new Date());
        if(timeResult < 1){
            return "201";
        }
        //把当月薪资数据转移到历史记录表中
        int MDResult = salaryHistoryMapper.insertMonthData();
        if(MDResult < 1){
            return "201";
        }
        //把薪资表的数据更新到基础状态
        int TBDResult = salaryMapper.updateToBaseData();
        if(TBDResult < 1){
            return "201";
        }
        return "200";
    }

    @Override
    @Transactional
    public String updateOneSalary(Salary salary) {
        salary.setUpdateTime(new Date());
        int result = salaryMapper.updateByPrimaryKeySelective(salary);
        if(result == 1){
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    public Map<String, Object> getDeptSalarys(String name, String time, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //获取当前用户（角色应为主管）的部门员工角色集合
        List<Long> roleIds = roleMapper.selectSimilarRole(UserContext.getCurrentUser().getRole().getDeptId());
        //移除主管的角色id
//        roleIds.remove(UserContext.getCurrentUser().getRoleId());
        //查询员工角色的用户id
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("roleId",roleIds);
        if(null != name && !"".equals(name)){
            criteria.andLike("name","%"+name+"%");
        }
        List<Long> userIds= userMapper.selectByExample(example).stream().map(User::getId).collect(Collectors.toList());

        //设置查询条件
        example = new Example(Salary.class);
        criteria = example.createCriteria();
        if(userIds.size() > 0){
            criteria.andIn("userId",userIds);
            if(null != time && !"".equals(time)){
                criteria.andGreaterThan("time",time);
            }
            //获取跳转页码，设置分页
            int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
            Page<Salary> page = PageHelper.startPage(pageno,5,true);
            List<Salary> salaries = salaryMapper.selectByExample(example);
            for (Salary s: salaries) {
                s.setUser(userMapper.selectByPrimaryKey(s.getUserId()));
            }
            //打包回传数据
            map.put("dataList",salaries);
            map.put("totalPage",page.getPages());
            map.put("currentPage",page.getPageNum());
        }
        else {
            map.put("dataList",new ArrayList<Salary>());
            map.put("totalPage",1);
            map.put("currentPage",1);
        }
        map.put("name",name);
        map.put("time",time);
        return map;
    }
}
