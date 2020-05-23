package com.faiop.core.controller;

import com.faiop.core.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
@RequestMapping("count")
public class StatisticsController {
    @Autowired
    StatisticsService statisticsService;

    /**
     * 根据条件统计各部门薪资
     * @param map
     * @return
     */
    @RequestMapping("deptSalaryStatistics")
    public Map<String,Object> deptSalaryStatistics(@RequestBody Map map){
        String type = map.get("type").toString();
        String year = map.get("year").toString();
        Map<String,Object> resultMap = null;
        if("year".equals(type)){
            resultMap = statisticsService.countDelptSalaryByYear(year);
        }
        else if("quarter".equals(type)){
            String quarter = map.get("quarter").toString();
            resultMap = statisticsService.countDelptSalaryByQuarter(year,quarter);
        }
        else if("month".equals(type)){
            String month = map.get("month").toString();
            resultMap = statisticsService.countDelptSalaryByMonth(year,month);
        }
        return resultMap;
    }

    /**
     * 获取历史记录的时间范围
     * @return
     */
    @RequestMapping("getYears")
    public List<Integer> getYears(){
        List<Integer> list = statisticsService.getYears();
        return list;
    }

    /**
     * 根据条件统计指定用户薪资
     * @param map
     * @return
     */
    @RequestMapping("empSalaryStatistics")
    public Map<String,Object> empSalaryStatistics(@RequestBody Map map){
        String id = map.get("id").toString();
        String beginTime = map.get("beginTime").toString();
        String endTime = map.get("endTime").toString();
        Map<String,Object> resultMap = statisticsService.countEmpSalary(id,beginTime,endTime);
        return resultMap;
    }

    /**
     * 根据条件统计指定企业资源
     * @param map
     * @return
     */
    @RequestMapping("getResourceHistory")
    public Map<String,Object> getResourceHistory(@RequestBody Map map){
        String resourceName = map.get("resourceName").toString();
        String type = map.get("type").toString();
        String beginTime = map.get("beginTime").toString();
        String endTime = map.get("endTime").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = statisticsService.countResourceHistory(resourceName,type,beginTime,endTime,pageNo);
        return resultMap;
    }

    /**
     * 根据条件查看员工简图
     * @param map
     * @return
     */
    @RequestMapping("getEmps")
    public Map<String,Object> getEmps(@RequestBody Map map){
        System.out.println("map = " + map);
        String name = map.get("name").toString();
        String state = map.get("state").toString();
        String dept = map.get("dept").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = statisticsService.countEmps(name,state,dept,pageNo);
        return resultMap;
    }

    /**
     * 获取所有的部门名称
     * @return
     */
    @RequestMapping("getDepts")
    public Set<String> getDepts(){
        return statisticsService.getAlldeptsAndEmps().keySet();
    }
}
