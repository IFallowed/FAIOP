package com.faiop.core.controller;

import com.faiop.core.pojo.Menu;
import com.faiop.core.pojo.User;
import com.faiop.core.service.UserService;
import com.faiop.core.util.ParentItem;
import com.faiop.core.util.UserContext;
import com.faiop.core.util.token.annotation.UserLoginToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
public class MainController {
    @Autowired
    UserService userService;
    /**
     * 获取登录用户信息
     * @return
     */
    @RequestMapping("/main/getUserInfo")
    @ResponseBody
    public Map<String,Object> getUserInfo(){
        Map<String,Object> map = new HashMap<>();
        User user = UserContext.getCurrentUser();
        log.info(user.toString());
        map.put("user", user);
        Map<Long, ParentItem> menuList = userService.getMenuList(user.getRoleId());
//        log.info(menuList.toString());
        map.put("menuList",menuList);
//        log.info(map.toString());
        return map;
    }

    @RequestMapping("/")
    public void toLogin(){
        try {
            UserContext.getResponse().sendRedirect("/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
