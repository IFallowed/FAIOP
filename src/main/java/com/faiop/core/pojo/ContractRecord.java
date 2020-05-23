package com.faiop.core.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_scRecord")
public class ContractRecord {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "scId")
    private Long scId;
    private String state;
    @Column(name = "approvalId")
    private Long approvalId;
    @Column(name = "originateId")
    private Long originateId;
    private String note;
    @Column(name = "updateTime")
    private Date updateTime;

    @Transient
    private SaleContract saleContract;
    @Transient
    private User approval;
    @Transient
    private User originate;
}
