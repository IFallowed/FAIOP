package com.faiop.core.service.impl;

import com.faiop.core.mapper.*;
import com.faiop.core.pojo.Resource;
import com.faiop.core.pojo.ResourceHistory;
import com.faiop.core.pojo.ResourceRecord;
import com.faiop.core.pojo.User;
import com.faiop.core.service.ResourceService;
import com.faiop.core.util.UserContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Description:
 * @Author RM
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    ResourceMapper resourceMapper;
    @Autowired
    ResourceRecordMapper resourceRecordMapper;
    @Autowired
    ResourceHistoryMapper resourceHistoryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    SalaryMapper salaryMapper;

    @Override
    @Transactional
    public String deleteResourceRecord(Long id) {
        int result = resourceRecordMapper.deleteByPrimaryKey(id);
        if(result == 1){
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    public Map<String,Object> getResourceRecords(String[] type, String state, Long userId) {
        Map<String,Object> map = new HashMap<>();
        //设置查询条件
        Example example = new Example(ResourceRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != type && type.length != 0){
            criteria.andIn("type", Arrays.asList(type));
        }
        if(null != userId){
            criteria.andEqualTo("originateId",userId);
        }
        if(null != state){
        criteria.andEqualTo("state",state);
        }
        List<ResourceRecord> resourceRecords = resourceRecordMapper.selectByExample(example);
        for(ResourceRecord rr : resourceRecords){
            //为每个资源审核对象补充其依赖对象数据，达到联合查询的目的
            rr.setResource(resourceMapper.selectByPrimaryKey(rr.getResourceId()));
            rr.setOriginate(userMapper.selectByPrimaryKey(rr.getOriginateId()));
            rr.setApproval(rr.getApprovalId() == null ? null : userMapper.selectByPrimaryKey(rr.getApprovalId()));
            rr.getOriginate().setRole(roleMapper.selectByPrimaryKey(rr.getOriginate().getRoleId()));
        }
        System.out.println("resourceRecords = " + resourceRecords);
        map.put("dataList",resourceRecords);
        return map;
    }

    @Override
    @Transactional
    public String returnResource(Long id, String state) {
        ResourceRecord resourceRecord = resourceRecordMapper.selectByPrimaryKey(id);
        if(resourceRecord.getState().equals(state)) {    //验证资源申请的状态是否一致
            resourceRecord.setState("0");
            resourceRecord.setType("2");
            resourceRecord.setApprovalId(null);
            int result = resourceRecordMapper.updateByPrimaryKeySelective(resourceRecord);
            if(result != 1){
                return "201";
            }
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    @Transactional
    public String approveResource(Long id, String state, String result, String note) {
        ResourceRecord resourceRecord = resourceRecordMapper.selectByPrimaryKey(id);
        if(resourceRecord.getState().equals(state)){    //验证资源申请的状态是否一致
            if("1".equals(result)){     //审批通过
                //修改状态
                resourceRecord.setState("1");
                //添加审批人
                resourceRecord.setApprovalId(UserContext.getCurrentUser().getId());
                //修改资源库存数据
                Resource resource = resourceMapper.selectByPrimaryKey(resourceRecord.getResourceId());
                if(resourceRecord.getType().equals("1")){
                    resource.setAmount(resource.getAmount() - resourceRecord.getAmount());
                }
                else if(resourceRecord.getType().equals("2")){
                    resource.setAmount(resource.getAmount() + resourceRecord.getAmount());
                }
                else if(resourceRecord.getType().equals("3")){
                    resource.setAmount(resource.getAmount() + resourceRecord.getAmount());
                    //审批成功，则将采购花费添加到用户的采购补助中
                    int PSResult = salaryMapper.updatePurchaseSalaryByUserId(resourceRecord.getMoney(),UserContext.getCurrentUser().getId());
                    if(PSResult != 1){
                        return "201";
                    }
                }
                int resourceResult = resourceMapper.updateByPrimaryKeySelective(resource);
                if(resourceResult != 1){
                    return "201";
                }
                //写入资源申请日志
                ResourceHistory resourceHistory = new ResourceHistory();
                resourceHistory.setResourceId(resourceRecord.getResourceId());
                resourceHistory.setAmount(resourceRecord.getAmount());
                resourceHistory.setMoney(resourceRecord.getMoney());
                resourceHistory.setApprovalId(resourceRecord.getApprovalId());
                resourceHistory.setOriginateId(resourceRecord.getOriginateId());
                resourceHistory.setType(resourceRecord.getType());
                resourceHistory.setNote(resourceRecord.getNote());
                resourceHistory.setCreateTime(new Date());
                int historyResult = resourceHistoryMapper.insert(resourceHistory);
                if(historyResult != 1){
                    return "201";
                }
                //修改备注
                resourceRecord.setNote(note);
                int recordResult = resourceRecordMapper.updateByPrimaryKeySelective(resourceRecord);
                if(recordResult != 1){
                    return "201";
                }
            }
            else if("2".equals(result)){    //审批驳回
                //修改状态
                resourceRecord.setState("2");
                //添加审批人
                resourceRecord.setApprovalId(UserContext.getCurrentUser().getId());
                //修改备注
                resourceRecord.setNote(note);
                int recordResult = resourceRecordMapper.updateByPrimaryKeySelective(resourceRecord);
                if(recordResult != 1){
                    return "201";
                }
            }
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    @Transactional
    public String purchaseResource(String name, Integer amount, BigDecimal money, String unit, String note) {
        ResourceRecord resourceRecord = new ResourceRecord();
        //查询数据库里有没有这种资源
        Resource resource = new Resource();
        resource.setName(name);
        List<Resource> resources = resourceMapper.select(resource);
        if(resources.size() == 0){
            //没有就新建一个数量为0的资源保存到数据库中
            resource.setAmount(0);
            resource.setUnit(unit);
            int resourceresult = resourceMapper.insert(resource);
            if(resourceresult != 1){
                return "201";
            }
            System.out.println("purchaseResource: id="+resource.getId());
            //新建一条资源采购申请记录
            resourceRecord.setResourceId(resource.getId());
            resourceRecord.setAmount(amount);
            resourceRecord.setMoney(money);
            resourceRecord.setState("0");
            resourceRecord.setType("3");
            resourceRecord.setOriginateId(UserContext.getCurrentUser().getId());
            resourceRecord.setNote(note);
            resourceRecord.setUpdateTime(new Date());
            int RecordResult = resourceRecordMapper.insert(resourceRecord);
            if(RecordResult != 1){
                return "201";
            }
            return "200";
        }
        else if(resources.size() == 1){
            //新建一条资源采购申请记录
            resourceRecord.setResourceId(resources.get(0).getId());
            resourceRecord.setAmount(amount);
            resourceRecord.setMoney(money);
            resourceRecord.setState("0");
            resourceRecord.setType("3");
            resourceRecord.setOriginateId(UserContext.getCurrentUser().getId());
            resourceRecord.setNote(note);
            resourceRecord.setUpdateTime(new Date());
            int RecordResult = resourceRecordMapper.insert(resourceRecord);
            if(RecordResult != 1){
                return "201";
            }
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    @Transactional
    public String applyResource(Long id,Integer amount,String note) {
        System.out.println("id = " + id + ", amount = " + amount + ", note = " + note);
        ResourceRecord resourceRecord = new ResourceRecord();
        resourceRecord.setResourceId(id);   //设置资源id
        resourceRecord.setAmount(amount);   //设置申请数量
        resourceRecord.setNote(note);       //设置申请备注
        resourceRecord.setUpdateTime(new Date());   //设置申请时间
        resourceRecord.setState("0");       // 设置申请状态为'申请中'
        resourceRecord.setType("1");        //设置类型为'资源借还'
        resourceRecord.setOriginateId(UserContext.getCurrentUser().getId()); //设置申请人id
        int applyResult = resourceRecordMapper.insert(resourceRecord);
        if(1 == applyResult){
            return "200";
        }
        else{
            return "201";
        }
    }

    @Override
    public Map<String, Object> getResourceList(String queryName, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Page<Resource> page = PageHelper.startPage(pageno,5,true);
        //设置查询条件
        Example example = new Example(Resource.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != queryName && !"".equals(queryName)){
            criteria.andLike("name","%"+queryName+"%");
        }
        //资源数量大于0的才会被显示到页面上
        criteria.andGreaterThan("amount",0);
        List<Resource> resources = resourceMapper.selectByExample(example);
        //打包回传数据
        map.put("dataList",resources);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("queryName",queryName);
        return map;
    }
}
