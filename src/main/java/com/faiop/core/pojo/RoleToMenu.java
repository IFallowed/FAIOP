package com.faiop.core.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_roleToMenu")
public class RoleToMenu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "roleId")
    private Long roleId;
    @Column(name = "menuId")
    private Long menuId;

    @Transient
    private Role role;
    @Transient
    private Menu menu;
}
