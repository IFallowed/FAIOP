package com.faiop.core.controller;

import com.faiop.core.pojo.Salary;
import com.faiop.core.service.SalaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
@RequestMapping("salary")
public class SalaryController {
    @Autowired
    SalaryService salaryService;

    /**
     * 获取当月部门员工的薪资列表
     * @param map
     * @return
     */
    @RequestMapping("getDeptSalarys")
    public Map<String,Object> getDeptSalarys(@RequestBody Map map){
        String name = map.get("name").toString();
        String time = map.get("time").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = salaryService.getDeptSalarys(name,time,pageNo);
        return resultMap;
    }

    /**
     * 修改员工的薪资
     * @param salary
     * @return
     */
    @RequestMapping("updateOneSalary")
    public String updateOneSalary(@RequestBody Salary salary){
//        System.out.println("salary" + salary);
        String result = salaryService.updateOneSalary(salary);
        return result;
    }

//    /**
//     * 下发本月的工资单
//     * @return
//     */
//    @RequestMapping("issueSalarys")
//    public String issueSalarys(){
//        String result = salaryService.issueSalarys();
//        return result;
//    }

    /**
     * 查看个人当月薪资
     * @return
     */
    @RequestMapping("getNowSalary")
    public Map<String,Object> getNowSalary(){
        Map<String,Object> resultMap = salaryService.getNowSalary();
        return resultMap;
    }

    /**
     * 查看个人历史薪资
     * @param map
     * @return
     */
    @RequestMapping("getHistorySalarys")
    public Map<String,Object> getHistorySalarys(@RequestBody Map map){
        String time = map.get("time").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = salaryService.getHistorySalarys(time,pageNo);
        return resultMap;
    }
}
