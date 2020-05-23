package com.faiop.core.controller;

import com.faiop.core.pojo.Role;
import com.faiop.core.pojo.User;
import com.faiop.core.service.UserService;
import com.faiop.core.service.impl.TokenService;
import com.faiop.core.util.CommonConstant;
import com.faiop.core.util.FileUpLoad;
import com.faiop.core.util.ParentItem;
import com.faiop.core.util.UserContext;
import com.faiop.core.util.token.TokenUtil;
import com.faiop.core.util.token.annotation.UserLoginToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ServletContext servletContext;
//    @Autowired
//    TokenService tokenService;

    /**
     * 处理用户登录请求
     * @param map
     * @return
     */
    @RequestMapping("/toLogin")
    public String login(@RequestBody Map<String,Object> map){
        log.info(map.toString());
        String result = userService.login(map.get("account").toString(), map.get("password").toString());
//        User user = UserContext.getCurrentUser();
//        String token = tokenService.getToken(user);
//        Cookie cookie = new Cookie("token", token);
//        cookie.setPath("/");
//        UserContext.getResponse().addCookie(cookie);
        return result;
    }

    /**
     * 处理用户修改密码请求
     * @param map
     * @return
     */
    @RequestMapping("/toUpdatePwd")
    public String updatePwd(@RequestBody Map<String,Object> map){
        log.info(map.toString());
        String result = userService.updatePwd(map.get("oldPwd").toString(),map.get("newPwd").toString());
        return result;
    }

    /**
     * 头像上传
     * @param file
     * @return
     */
    @RequestMapping("/uploadImg")
    public String uploadImg(@RequestParam("file")MultipartFile file){
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
        if(Arrays.asList(FileUpLoad.imgSuffix).contains(suffix)){
            String avatarName = FileUpLoad.saveFile(file, CommonConstant.PATH_UPLOAD_AVATAR);
            String result = userService.updateImg("/upload/avatar/" + avatarName);
            return result;
        }
        else {
            return "202";
        }
    }

    /**
     * 获取登录用户信息
     * @return
     */
    @RequestMapping("/getUserInfo")
    public Map<String,Object> getUserInfo(){
        Map<String,Object> map = new HashMap<>();
        User user = UserContext.getCurrentUser();
        log.info(user.toString());
        map.put("user", user);
        return map;
    }

    /**
     * 跟新登录用户信息
     * @return
     */
    @RequestMapping("/updateUser")
    public String updateUser(@RequestBody User user){
        log.info(user.toString());
        user.setId(UserContext.getCurrentUser().getId());
        String result = userService.updateUser(user);
        return result;
    }

    /**
     * 获取职位列表
     * @return
     */
    @RequestMapping("/getRoleList")
    public List<Role> getRoleList(){
        List<Role> roleList = userService.getRoleList();
        return roleList;
    }

    /**
     * 注册用户
     * @return
     */
    @RequestMapping("/registUser")
    public String registUser(@RequestBody User user){
        log.info("registUser" + user.toString());
        String result = userService.registUser(user);
        return result;
    }

    /**
     * 渲染部门员工列表
     * @return
     */
    @RequestMapping("/getDeptUsers")
    public Map<String,Object> getDeptUsers(@RequestBody Map map){
        log.info("getDeptUsers" + map.toString());
        String account = map.get("queryAccount").toString();
        String name = map.get("queryName").toString();
        String state = map.get("queryState").toString();
        String pageNo = map.get("pageNo").toString();
        Map<String,Object> resultMap = userService.getDeptUsers(account,name,state,pageNo);
        System.out.println("getDeptUsers:" + map.get("dataList"));
        return resultMap;
    }
    /**
     * 跟新员工状态
     * @return
     */
    @RequestMapping("/updateUserState")
    public String updateUserState(@RequestBody User user){
        log.info(user.toString());
        String result = userService.updateUserState(user);
        return result;
    }

    /**
     * 用户退出
     * @return
     */
    @RequestMapping("/quit")
    public void quit(){
        UserContext.setCurrentUser(null);
        try {
            UserContext.getResponse().sendRedirect("/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
