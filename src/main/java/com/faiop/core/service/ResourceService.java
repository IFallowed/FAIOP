package com.faiop.core.service;

import com.faiop.core.pojo.Resource;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
public interface ResourceService {
    /**
     * 获取资源列表
     * @param queryName
     * @param pageNo
     * @return
     */
    Map<String, Object> getResourceList(String queryName, String pageNo);

    /**
     * 申请资源
      * @param id
     * @param amount
     * @param note
     * @return
     */
    String applyResource(Long id,Integer amount,String note);

    /**
     * 获取资源审核列表
     * @param type
     * @param state
     * @param userId
     * @return
     */
    Map<String,Object> getResourceRecords(String[] type,String state, Long userId);

    /**
     *  资源审批
     * @param id
     * @param state
     * @param result
     * @param note
     * @return
     */
    String approveResource(Long id, String state, String result, String note);

    /**
     * 删除资源申请
     * @param id
     * @return
     */
    String deleteResourceRecord(Long id);

    /**
     * 申请归还资源
     * @param id
     * @param state
     * @return
     */
    String returnResource(Long id, String state);

    /**
     *
     * @param name
     * @param amount
     * @param money
     *  @param unit
     * @param note
     * @return
     */
    String purchaseResource(String name, Integer amount, BigDecimal money, String unit, String note);
}
