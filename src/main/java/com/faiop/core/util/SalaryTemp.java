package com.faiop.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author RM
 */
@Component
public class SalaryTemp {
    public static BigDecimal SALARY_BASESALARY;
    public static BigDecimal SALARY_TRAFFICSALARY;
    public static BigDecimal SALARY_HOUSESALARY;
    public static BigDecimal SALARY_FOODSALARY;
    public static BigDecimal SALARY_PROVIDENTFUND;
    public static BigDecimal SALARY_OVERTIME;
    public static BigDecimal SALARY_SICKLEAVE;
    public static BigDecimal SALARY_UNATTEND;
    public static BigDecimal SALARY_MARRAY2FUNERAL;
    public static BigDecimal SALARY_LATE;

    @Value("${salary.baseSalary}")
    public void setSalaryBasesalary(BigDecimal salaryBasesalary) {
        SALARY_BASESALARY = salaryBasesalary;
    }

    @Value("${salary.trafficSalary}")
    public void setSalaryTrafficsalary(BigDecimal salaryTrafficsalary) {
        SALARY_TRAFFICSALARY = salaryTrafficsalary;
    }

    @Value("${salary.houseSalary}")
    public void setSalaryHousesalary(BigDecimal salaryHousesalary) {
        SALARY_HOUSESALARY = salaryHousesalary;
    }

    @Value("${salary.foodSalary}")
    public void setSalaryFoodsalary(BigDecimal salaryFoodsalary) {
        SALARY_FOODSALARY = salaryFoodsalary;
    }

    @Value("${salary.providentFund}")
    public void setSalaryProvidentfund(BigDecimal salaryProvidentfund) {
        SALARY_PROVIDENTFUND = salaryProvidentfund;
    }

    @Value("${salary.overTime}")
    public void setSalaryOvertime(BigDecimal salaryOvertime) {
        SALARY_OVERTIME = salaryOvertime;
    }

    @Value("${salary.sickLeave}")
    public void setSalarySickleave(BigDecimal salarySickleave) {
        SALARY_SICKLEAVE = salarySickleave;
    }

    @Value("${salary.unattend}")
    public void setSalaryUnattend(BigDecimal salaryUnattend) {
        SALARY_UNATTEND = salaryUnattend;
    }

    @Value("${salary.marray2funeral}")
    public void setSalaryMarray2funeral(BigDecimal salaryMarray2funeral) {
        SALARY_MARRAY2FUNERAL = salaryMarray2funeral;
    }

    @Value("${salary.late}")
    public void setSalaryLate(BigDecimal salaryLate) {
        SALARY_LATE = salaryLate;
    }

}
