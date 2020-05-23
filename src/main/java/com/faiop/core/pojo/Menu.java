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
@Table(name = "t_menu")
public class Menu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private String url;
    @Column(name = "parentId")
    private Long parentId;
}
