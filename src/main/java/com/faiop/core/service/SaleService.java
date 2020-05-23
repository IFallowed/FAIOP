package com.faiop.core.service;

import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
public interface SaleService {
    /**
     * 上传合同数据
     * @param consumer
     * @param entityPic
     * @param vloc
     * @return
     */
    String uploadContract(String consumer, String entityPic, String vloc);

    /**
     * 获取合同申请记录
     * @param state
     * @param userId
     * @return
     */
    Map<String, Object> getContractRecords(String[] state, Long userId);

    /**
     * 处理合同的审批
     * @param id
     * @param state
     * @param result
     * @param note
     * @return
     */
    String approveContract(Long id, String state, String result, String note);

    /**
     * 持久化保存上传记录
     * @param id
     * @return
     */
    String savefromRecord(Long id);

    /**
     * 重新上传
     * @param id
     * @return
     */
    String reUpload(Long id);

    /**
     * 合同下载
     * @param id
     */
    void download(Long id);

    /**
     * 个人业绩
     * @param id
     * @param consumer
     * @param time
     * @param pageNo
     * @return
     */
    Map<String, Object> getMySaleContracts(String id, String consumer, String time, String pageNo);

    /**
     * 部门业绩
     * @param seller
     * @param consumer
     * @param time
     * @param pageNo
     * @return
     */
    Map<String, Object> getDeptSaleContracts(String seller, String consumer, String time, String pageNo);
}
