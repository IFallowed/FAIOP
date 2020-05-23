package com.faiop.core.controller;

import com.faiop.core.pojo.Resource;
import com.faiop.core.service.ResourceService;
import com.faiop.core.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    /**
     * 获取资源列表
     * @param map
     * @return
     */
    @RequestMapping("getResourceList")
    public Map<String,Object> getResourceList(@RequestBody Map map){
        log.info("getDpetUsers()--"+map.toString());
        Map<String,Object> resultMap = resourceService.getResourceList(map.get("queryName").toString(),map.get("pageNo").toString());
        return resultMap;
    }

    /**
     * 资源申请
     * @param map
     * @return
     */
    @RequestMapping("applyResource")
    public String applyResource(@RequestBody Map map){
        log.info("applyResource()--" + map.toString());
        String result = resourceService.applyResource(Long.valueOf(map.get("id").toString()),
                Integer.valueOf(map.get("amount").toString()),
                map.get("note").toString());
        return result;
    }

    /**
     * 请求资源审批列表
     * @param requestType
     * @return
     */
    @RequestMapping("getResourceRecords/{requestType}")
    public Map<String,Object> getResourceRecords(@PathVariable("requestType") String requestType){
        String[] type = null;
        Long userId = null;
        String state = null;
        Long roleId = UserContext.getCurrentUser().getRoleId();
        if(requestType.equals("approving")){
            if(6 == roleId){
                type = new String[]{"1","2"};
            }
            else if(7 == roleId){
                type = new String[]{"1","2","3"};
            }
            state = "0";
        }
        else if(requestType.equals("apply2borrow")){
            if(6 == roleId || 7 == roleId || 1 == roleId){
                type = new String[]{"1","2","3"};
            }
            else {
                type = new String[]{"1","2"};
            }
            userId = UserContext.getCurrentUser().getId();
        }
        Map<String, Object> resultMap = resourceService.getResourceRecords(type,state, userId);
        return resultMap;
    }

    /**
     * 审批各类申请
     * @param map
     * @return
     */
    @RequestMapping("approveResource")
    public String approveResource(@RequestBody Map map){
        String result = resourceService.approveResource(
                Long.valueOf(map.get("id").toString()),
                map.get("state").toString(),
                map.get("result").toString(),
                map.get("note").toString()
                );
        return result;
    }

    /**
     * 删除资源申请
     * @param map
     * @return
     */
    @RequestMapping("deleteResourceRecord")
    public String deleteResourceRecord(@RequestBody Map map){
        String result = resourceService.deleteResourceRecord(Long.valueOf(map.get("id").toString()));
        return result;
    }
    /**
     * 申请归还资源
     * @param map
     * @return
     */
    @RequestMapping("returnResource")
    public String returnResource(@RequestBody Map map){
        String result = resourceService.returnResource(Long.valueOf(map.get("id").toString()),map.get("state").toString());
        return result;
    }

    /**
     * 申请采购资源
     * @param map
     * @return
     */
    @RequestMapping("purchaseResource")
    public String purchaseResource(@RequestBody Map map){
        String name = map.get("name").toString();
        Integer amount = Integer.valueOf(map.get("amount").toString());
        BigDecimal money = new BigDecimal(map.get("money").toString());
        String unit = map.get("unit").toString();
        String note = map.get("note").toString();
        String reuslt = resourceService.purchaseResource(name,amount,money,unit,note);
        return reuslt;
    }
}
