package com.faiop.core.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author RM
 */
@Data
@Table(name = "t_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String account;
    private String password;
    private String name;
    private Integer age;
    private String sex;
    private String phone;
    @Column(name = "roleId")
    private Long roleId;
    private String address;
    private String avatar;
    @Column(name = "createTime")
    private Date createTime;
    private String state;

    @Transient
    private Role role;


}
