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
@Table(name = "t_dept")
public class Dept {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
}
