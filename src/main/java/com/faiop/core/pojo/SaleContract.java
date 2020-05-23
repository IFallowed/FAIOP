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
@Table(name = "t_saleContract")
public class SaleContract {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "sellerId")
    private Long sellerId;
    private String consumer;
    @Column(name = "entityPic")
    private String entityPic;
    private String vloc;
    @Column(name = "createTime")
    private Date createTime;

    @Transient
    private User seller;
}
