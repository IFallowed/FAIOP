package com.faiop.core.util;

import com.faiop.core.pojo.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description: 封装当前登录用户的某些信息
 * @Author RM
 */
public class UserContext {
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
    //获取HttpSession对象
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static void setCurrentUser(User user) {
        getSession().setAttribute("user", user);
    }

    public static User getCurrentUser() {
        return (User) getSession().getAttribute("user");
    }

}
