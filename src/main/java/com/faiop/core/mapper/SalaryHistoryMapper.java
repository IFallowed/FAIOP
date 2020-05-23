package com.faiop.core.mapper;

import com.faiop.core.pojo.Salary;
import com.faiop.core.pojo.SalaryHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Description:
 * @Author RM
 */
public interface SalaryHistoryMapper extends Mapper<SalaryHistory> {
    @Insert("insert into t_salaryHistory (userId,baseSalary,attendenceDay,overTime,performance,fullAttendence," +
            "sickLeave,unattend,marray2funeral,late,disciplineFine,trafficSalary,houseSalary,foodSalary,purchaseSalary,providentFund,tax,updateTime)" +
            " select userId,baseSalary,attendenceDay,overTime,performance,fullAttendence," +
            "sickLeave,unattend,marray2funeral,late,disciplineFine,trafficSalary,houseSalary,foodSalary,purchaseSalary,providentFund,tax,updateTime" +
            " from t_salary")
    int insertMonthData();

    @Select("SELECT * FROM t_salaryHistory WHERE userId = #{id} ORDER BY ABS(NOW() - updateTime) ASC LIMIT 0,1")
    SalaryHistory selectRecentSalary(Long id);

    @Select("select updateTime from t_salaryHistory order by updateTime ${type} limit 0,1")
    String selectByTime(@Param("type") String type);
}
