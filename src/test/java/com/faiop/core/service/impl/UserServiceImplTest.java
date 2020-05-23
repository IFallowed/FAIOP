package com.faiop.core.service.impl;

import com.faiop.core.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Test
    public void getDeptUser(){
        Map<String, Object> deptUsers = userService.getDeptUsers("", "", "1", "1");
        System.out.println(deptUsers.get("dataList"));
    }
}