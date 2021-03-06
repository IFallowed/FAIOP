package com.faiop.core.pojo;

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
@Table(name = "t_resourceRecord")
public class ResourceRecord {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "resourceId")
    private Long resourceId;
    private Integer amount;
    private BigDecimal money;
    private String state;
    @Column(name = "approvalId")
    private Long approvalId;
    @Column(name = "originateId")
    private Long originateId;
    private String type;
    private String note;
    @Column(name = "updateTime")
    private Date updateTime;

    @Transient
    private Resource resource;
    @Transient
    private User approval;
    @Transient
    private User originate;
}
