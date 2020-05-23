package com.faiop.core.mapper;

import com.faiop.core.pojo.Salary;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author RM
 */
public interface SalaryMapper extends Mapper<Salary> {
    @Update("update t_salary set updateTime = #{date}")
    int updateAllTime(Date date);

    @Update("update t_salary set attendenceDay = 0,overTime = 0,performance = 0,fullAttendence = 0,"+
            "sickLeave = 0,unattend = 0,marray2funeral = 0,late = 0,disciplineFine = 0,purchaseSalary = 0")
    int updateToBaseData();

    @Update("update t_salary set purchaseSalary = purchaseSalary + #{money} where userId = #{id}")
    int updatePurchaseSalaryByUserId(BigDecimal money, Long id);
}
