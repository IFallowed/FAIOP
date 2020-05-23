package com.faiop.core.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_resource")
public class Resource {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private Integer amount;
    private String unit;
}
