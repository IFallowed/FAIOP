package com.faiop.core.service;

import com.faiop.core.pojo.Role;
import com.faiop.core.pojo.User;
import com.faiop.core.util.ParentItem;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
public interface UserService {
    /**
     * 用户登录检测
     * @param account
     * @param password
     * @return
     */
    String login(String account, String password);

    /**
     * 获取当前角色对应的菜单列表
     * @param roleId
     * @return
     */
    Map<Long, ParentItem> getMenuList(Long roleId);

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    User queryById(Long id);

    /**
     * 修改用户的密码
     * @param oldPwd
     * @param newPwd
     * @return
     */
    String updatePwd(String oldPwd,String newPwd);

    /**
     * 用户头像上传
     * @param url
     * @return
     */
    String updateImg(String url);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    String updateUser(User user);

    /**
     * 获取职位列表
     * @return
     */
    List<Role> getRoleList();

    /**
     * 注册用户
     * @param user
     * @return
     */
    String registUser(User user);

    /**
     * 渲染部门员工列表
     * @param account
     * @param name
     * @param state
     * @param pageNo
     * @return
     */
    Map<String, Object> getDeptUsers(String account, String name, String state, String pageNo);

    /**
     * 更新员工状态
     * @param user
     * @return
     */
    String updateUserState(User user);
}
