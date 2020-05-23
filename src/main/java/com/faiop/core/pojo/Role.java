package com.faiop.core.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_role")
public class Role {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    @Column(name = "deptId")
    private Long deptId;
}
