package com.faiop.core.mapper;

import com.faiop.core.pojo.Role;
import com.faiop.core.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {
    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Test
    public void addUser(){
        User user = new User();
        user.setAccount("admin");
        user.setPassword("admin");
        user.setRoleId(1l);
        userMapper.insert(user);
    }

    @Test
    public void getUser(){
        User user = userMapper.selectByPrimaryKey(1l);
//        User user = userMapper.select2RoleByKey(1l);
        user.setRole(roleMapper.selectByPrimaryKey(user.getRoleId()));
        System.out.println(user.toString());
    }

}