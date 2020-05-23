package com.faiop.core.pojo;

import com.faiop.core.util.SalaryTemp;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_salaryHistory")
public class SalaryHistory {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "userId")
    private Long userId;
    @Column(name = "baseSalary")
    private BigDecimal baseSalary;
    @Column(name = "attendenceDay")
    private Integer attendenceDay;
    @Column(name = "overTime")
    private Integer overTime;
    private BigDecimal performance;
    @Column(name = "fullAttendence")
    private BigDecimal fullAttendence;
    @Column(name = "sickLeave")
    private Integer sickLeave;
    private Integer unattend;
    private Integer marray2funeral;
    private Integer late;
    @Column(name = "disciplineFine")
    private BigDecimal disciplineFine;
    @Column(name = "trafficSalary")
    private BigDecimal trafficSalary;
    @Column(name = "houseSalary")
    private BigDecimal houseSalary;
    @Column(name = "foodSalary")
    private BigDecimal foodSalary;
    @Column(name = "purchaseSalary")
    private BigDecimal purchaseSalary;
    @Column(name = "providentFund")
    private BigDecimal providentFund;
    private BigDecimal tax;
    @Column(name = "updateTime")
    private Date updateTime;

    @Transient
    private User user;

    public SalaryHistory(){
        this.baseSalary = SalaryTemp.SALARY_BASESALARY;
        this.trafficSalary = SalaryTemp.SALARY_TRAFFICSALARY;
        this.houseSalary = SalaryTemp.SALARY_HOUSESALARY;
        this.foodSalary = SalaryTemp.SALARY_FOODSALARY;
        this.providentFund = SalaryTemp.SALARY_PROVIDENTFUND;
    }

    public BigDecimal getOverTimeSalary() {
        return SalaryTemp.SALARY_OVERTIME.multiply(new BigDecimal(this.overTime.toString()));
    }

    public BigDecimal getCommuteSalary() {
        return this.baseSalary.add(getOverTimeSalary()).add(this.performance).add(this.fullAttendence)
                .add(getUnattendSalary());
    }

    public BigDecimal getUnattendSalary() {
        return (SalaryTemp.SALARY_SICKLEAVE.multiply(new BigDecimal(this.sickLeave.toString())))
                .add(SalaryTemp.SALARY_UNATTEND.multiply(new BigDecimal(this.unattend.toString())))
                .add(SalaryTemp.SALARY_MARRAY2FUNERAL.multiply(new BigDecimal(this.marray2funeral.toString())))
                .add(SalaryTemp.SALARY_LATE.multiply(new BigDecimal(this.late.toString())));
    }

    public BigDecimal getOtherSalary() {
        return this.disciplineFine.add(this.trafficSalary).add(this.houseSalary).add(this.foodSalary).add(this.purchaseSalary).add(this.providentFund);
    }

    public BigDecimal getTotal() {
        return getCommuteSalary().add(getOtherSalary()).subtract(this.tax);
    }
}
