package com.faiop.core.controller;

import com.faiop.core.service.SaleService;
import com.faiop.core.util.CommonConstant;
import com.faiop.core.util.FileUpLoad;
import com.faiop.core.util.PathUtil;
import com.faiop.core.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
@RequestMapping("sale")
public class SaleController {
    @Autowired
    ServletContext servletContext;
    @Autowired
    SaleService saleService;

    /**
     * 获取上传者的名称
     * @return
     */
    @RequestMapping("getSellerName")
    public String getSellerName(){
        return UserContext.getCurrentUser().getName();
    }

    /**
     * 处理合同上传
     * @param consumer
     * @param img
     * @param doc
     * @return
     * @throws FileNotFoundException
     */
    @RequestMapping("uploadContract")
    public String uploadContract(String consumer, MultipartFile img,MultipartFile doc) throws FileNotFoundException {
        System.out.println("img.getOriginalFilename() = " + img.getOriginalFilename());
        System.out.println("doc.getOriginalFilename() = " + doc.getOriginalFilename());
        String imgSuffix = img.getOriginalFilename().substring(img.getOriginalFilename().indexOf(".") + 1);
        String docSuffix = doc.getOriginalFilename().substring(doc.getOriginalFilename().indexOf(".") + 1);
        if(!Arrays.asList(FileUpLoad.imgSuffix).contains(imgSuffix)){
            return "202";
        }
        if(!Arrays.asList(FileUpLoad.docSuffix).contains(docSuffix)){
            return "203";
        }

        //处理图片
        String imgName = FileUpLoad.saveFile(img, CommonConstant.PATH_UPLOAD_CONTRACT);
        //处理doc文档
        String docName = FileUpLoad.saveFile(doc, CommonConstant.PATH_UPLOAD_DOC);
        //处理普通数据
        String result = saleService.uploadContract(consumer,"/upload/contract/"+imgName,"/upload/doc/"+docName);
        return result;
    }

    /**
     * 获取合同审批列表
     * @param requestType
     * @return
     */
    @RequestMapping("getContractRecords/{requestType}")
    public Map<String,Object> getContractRecords(@PathVariable("requestType") String requestType){
        String[] state = null;
        Long userId = null;
        if("approving".equals(requestType)){
            state = new String[]{"0"};
        }
        else if("applyProgress".equals(requestType)){
            state = new String[]{"0","1","2"};
            userId = UserContext.getCurrentUser().getId();
        }
        Map<String,Object> map = saleService.getContractRecords(state,userId);
        return map;
    }

    /**
     * 处理合同的审批
     * @param map
     * @return
     */
    @RequestMapping("approveContract")
    public String approveContract(@RequestBody Map map){
        String result = saleService.approveContract(
                Long.valueOf(map.get("id").toString()),
                map.get("state").toString(),
                map.get("result").toString(),
                map.get("note").toString()
        );
        return result;
    }

    /**
     * 持久化保存上传记录
     * @param map
     * @return
     */
    @RequestMapping("savefromRecord")
    public String savefromRecord(@RequestBody Map map){
        String result = saleService.savefromRecord(Long.valueOf(map.get("id").toString()));
        return result;
    }

    /**
     * 重新上传
     * @param map
     * @return
     */
    @RequestMapping("reUpload")
    public String reUpload(@RequestBody Map map){
        String result = saleService.reUpload(Long.valueOf(map.get("id").toString()));
        return result;
    }

    /**
     * 合同下载
     * @param id
     */
    @RequestMapping("download/{id}")
    public void download(@PathVariable("id") Long id){
        saleService.download(id);
    }

    /**
     * 个人业绩
     * @param map
     * @return
     */
    @RequestMapping("getMySaleContracts")
    public Map<String,Object> getMySaleContracts(@RequestBody Map map){
        String id = map.get("id").toString();
        String consumer = map.get("consumer").toString();
        String time = map.get("createTime").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = saleService.getMySaleContracts(id,consumer,time,pageNo);
        return  resultMap;
    }

    /**
     * 个人业绩
     * @param map
     * @return
     */
    @RequestMapping("getDeptSaleContracts")
    public Map<String,Object> getDeptSaleContracts(@RequestBody Map map){
        String seller = map.get("seller").toString();
        String consumer = map.get("consumer").toString();
        String time = map.get("createTime").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = saleService.getDeptSaleContracts(seller,consumer,time,pageNo);
        return  resultMap;
    }
}
