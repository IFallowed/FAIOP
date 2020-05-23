package com.faiop.core.service.impl;

import com.faiop.core.mapper.ContractRecordMapper;
import com.faiop.core.mapper.SalaryMapper;
import com.faiop.core.mapper.SaleContractMapper;
import com.faiop.core.mapper.UserMapper;
import com.faiop.core.pojo.ContractRecord;
import com.faiop.core.pojo.Resource;
import com.faiop.core.pojo.SaleContract;
import com.faiop.core.service.SaleService;
import com.faiop.core.util.FileUpLoad;
import com.faiop.core.util.PathUtil;
import com.faiop.core.util.UserContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Description:
 * @Author RM
 */
@Service
public class SaleServiceImpl implements SaleService {
    @Autowired
    SaleContractMapper saleContractMapper;
    @Autowired
    ContractRecordMapper contractRecordMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public Map<String, Object> getMySaleContracts(String id, String consumer, String time, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //设置条件，只有持久变化保存在数据库里的记录才会显示
        List<Long> ids = contractRecordMapper.getscIdByState("3");
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Page<SaleContract> page = PageHelper.startPage(pageno,5,true);
        //设置查询条件
        Example example = new Example(SaleContract.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != id && !"".equals(id)){
            criteria.andEqualTo("id",id);
        }
        if(null != ids && ids.size() > 0){
            criteria.andIn("id",ids);
        }
        if(null != consumer && !"".equals(consumer)){
            criteria.andLike("consumer","%"+consumer+"%");
        }
        if(null != time && !"".equals(time)){
            criteria.andGreaterThan("createTime",time);
        }
        criteria.andEqualTo("sellerId",UserContext.getCurrentUser().getId());
        List<SaleContract> saleContracts = saleContractMapper.selectByExample(example);
        for(SaleContract sc : saleContracts){
            sc.setSeller(userMapper.selectByPrimaryKey(sc.getSellerId()));
        }
        //打包回传数据
        map.put("dataList",saleContracts);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("id",id);
        map.put("consumer",consumer);
        map.put("createTime",time);
        return map;
    }

    @Override
    public Map<String, Object> getDeptSaleContracts(String seller, String consumer, String time, String pageNo) {
        Map<String,Object> map = new HashMap<>();
        //设置条件，只有持久变化保存在数据库里的记录才会显示
        List<Long> scIds = contractRecordMapper.getscIdByState("3");
        List<Long> sellerIds = userMapper.getIdByName(seller);
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Page<SaleContract> page = PageHelper.startPage(pageno,5,true);
        //设置查询条件
        Example example = new Example(SaleContract.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != scIds && scIds.size() > 0){
            criteria.andIn("id",scIds);
        }
        if(null != seller && !"".equals(seller)){
            criteria.andIn("sellerId",sellerIds);
        }
        if(null != consumer && !"".equals(consumer)){
            criteria.andLike("consumer","%"+consumer+"%");
        }
        if(null != time && !"".equals(time)){
            criteria.andGreaterThan("createTime",time);
        }
        List<SaleContract> saleContracts = saleContractMapper.selectByExample(example);
        for(SaleContract sc : saleContracts){
            sc.setSeller(userMapper.selectByPrimaryKey(sc.getSellerId()));
        }
        //打包回传数据
        map.put("dataList",saleContracts);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("seller",seller);
        map.put("consumer",consumer);
        map.put("createTime",time);
        return map;
    }
    
    @Override
    public void download(Long id) {
        SaleContract saleContract = saleContractMapper.selectByPrimaryKey(id);
        //获取文件的保存路径
        String vloc = saleContract.getVloc();
        String dir = PathUtil.getProjectPath() + vloc;
        String docName = vloc.substring(vloc.indexOf("_") + 1,vloc.length());
        //设置响应头：下载文件
        UserContext.getResponse().setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        //设置建议保留名称
        UserContext.getResponse().setHeader("Content-Disposition","attachment;filename=" + docName);
        try {
            Files.copy(Paths.get(dir),UserContext.getResponse().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public String savefromRecord(Long id) {
        ContractRecord contractRecord = contractRecordMapper.selectByPrimaryKey(id);
        contractRecord.setState("3");
        int recordResult = contractRecordMapper.updateByPrimaryKeySelective(contractRecord);
        if(recordResult == 1){
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    @Transactional
    public String reUpload(Long id) {
        ContractRecord contractRecord = contractRecordMapper.selectByPrimaryKey(id);
        Long scId = contractRecord.getScId();
        SaleContract saleContract = saleContractMapper.selectByPrimaryKey(scId);
        //删除此合同的图片
        boolean imgResult = FileUpLoad.deleteFile(saleContract.getEntityPic());
        //删除此合同的doc文档
        boolean docResult = FileUpLoad.deleteFile(saleContract.getVloc());
        if(!imgResult || !docResult){
            return "201";
        }
        //删除合同审批记录
        int scResult = saleContractMapper.deleteByPrimaryKey(scId);
        if(scResult != 1){
            return "201";
        }
        //删除合同记录
        int resordResult = contractRecordMapper.delete(contractRecord);
        if(resordResult != 1){
            return "201";
        }
        return "200";
    }

    @Override
    @Transactional
    public String approveContract(Long id, String state, String result, String note) {
        ContractRecord contractRecord = contractRecordMapper.selectByPrimaryKey(id);
        if(contractRecord.getState().equals(state)){
            contractRecord.setState(result);
            contractRecord.setNote(note);
            contractRecord.setApprovalId(UserContext.getCurrentUser().getId());
            int recordResult = contractRecordMapper.updateByPrimaryKeySelective(contractRecord);
            if(recordResult == 1){
                return "200";
            }
            else {
                return "201";
            }
        }
        else{
            return "201";
        }
    }

    @Override
    public Map<String, Object> getContractRecords(String[] state,Long userId) {
        //设置查询条件
        Example example = new Example(ContractRecord.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != state){
            criteria.andIn("state", Arrays.asList(state));
        }
        if(null != userId){
            criteria.andEqualTo("originateId",UserContext.getCurrentUser().getId());
        }
        List<ContractRecord> contractRecords = contractRecordMapper.selectByExample(example);
        for (ContractRecord cr : contractRecords){
            //补充依赖的数据对象
            cr.setSaleContract(saleContractMapper.selectByPrimaryKey(cr.getScId()));
            cr.setOriginate(userMapper.selectByPrimaryKey(cr.getOriginateId()));
            cr.setApproval(cr.getApprovalId() == null ? null : userMapper.selectByPrimaryKey(cr.getApprovalId()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("dataList",contractRecords);
        return map;
    }

    @Override
    @Transactional
    public String uploadContract(String consumer, String entityPic, String vloc) {
        //向合同表内添加一条新的合同记录
        SaleContract saleContract = new SaleContract();
        saleContract.setSellerId(UserContext.getCurrentUser().getId());
        saleContract.setConsumer(consumer);
        saleContract.setEntityPic(entityPic);
        saleContract.setVloc(vloc);
        saleContract.setCreateTime(new Date());
        int result = saleContractMapper.insert(saleContract);
        if(result != 1){
            return "201";
        }
        //向合同审批表内添加一条新的审批记录
        ContractRecord contractRecord = new ContractRecord();
        contractRecord.setScId(saleContract.getId());
        contractRecord.setState("0");
        contractRecord.setOriginateId(UserContext.getCurrentUser().getId());
        contractRecord.setUpdateTime(new Date());
        int cRecord = contractRecordMapper.insert(contractRecord);
        if (cRecord != 1){
            return "201";
        }
        return "200";
    }
}
