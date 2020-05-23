package com.faiop.core.service;

import com.faiop.core.pojo.Salary;

import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
public interface SalaryService {
    /**
     * 获取当月部门员工的薪资列表
     * @param name
     * @param time
     * @param pageNo
     * @return
     */
    Map<String, Object> getDeptSalarys(String name, String time, String pageNo);

    /**
     * 修改员工的薪资
     * @param salary
     * @return
     */
    String updateOneSalary(Salary salary);

//    /**
//     * 下发本月的工资单
//     * @return
//     */
//    String issueSalarys();

    /**
     * 查看个人当月薪资
     * @return
     */
    Map<String, Object> getNowSalary();

    /**
     * 查看个人历史薪资
     * @param time
     * @param pageNo
     * @return
     */
    Map<String, Object> getHistorySalarys(String time, String pageNo);
}
