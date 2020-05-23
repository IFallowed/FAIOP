package com.faiop.core.service;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
public interface StatisticsService {
    /**
     * 根据年份计算各部门工资
     * @param year
     * @return
     */
    Map<String, Object> countDelptSalaryByYear(String year);

    /**
     * 根据年份+季度计算各部门工资
     * @param year
     * @param quarter
     * @return
     */
    Map<String, Object> countDelptSalaryByQuarter(String year, String quarter);

    /**
     * 根据年份+月份计算各部门工资
     * @param year
     * @param month
     * @return
     */
    Map<String, Object> countDelptSalaryByMonth(String year, String month);

    /**
     * 获取历史记录的时间范围
     * @return
     */
    List<Integer> getYears();

    /**
     * 根据条件统计指定用户薪资
     * @param id
     * @param beginTime
     * @param endTime
     * @return
     */
    Map<String, Object> countEmpSalary(String id, String beginTime, String endTime);

    /**
     * 根据条件统计指定企业资源
     * @param resourceName
     * @param type
     * @param beginTime
     * @param endTime
     * @param pageNo
     * @return
     */
    Map<String, Object> countResourceHistory(String resourceName, String type, String beginTime, String endTime, String pageNo);

    /**
     * 根据条件查看员工简图
     * 根据条件查看员工简图
     * @param name
     * @param state
     * @param dept
     * @param pageNo
     * @return
     */
    Map<String, Object> countEmps(String name, String state, String dept, String pageNo);

    /**
     * 对所有员工按部门名称分类存储到map集合里
     * @return
     */
    Map<String, List<Long>> getAlldeptsAndEmps();
}
