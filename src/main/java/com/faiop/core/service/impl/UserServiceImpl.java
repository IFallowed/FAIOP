package com.faiop.core.service.impl;

import com.faiop.core.mapper.*;
import com.faiop.core.pojo.Menu;
import com.faiop.core.pojo.Role;
import com.faiop.core.pojo.Salary;
import com.faiop.core.pojo.User;
import com.faiop.core.service.UserService;
import com.faiop.core.util.MenuModel;
import com.faiop.core.util.ParentItem;
import com.faiop.core.util.UserContext;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleToMenuMapper roleToMenuMapper;
    @Autowired
    MenuMapper menuMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    SalaryMapper salaryMapper;

    @Override
    public Map<String, Object> getDeptUsers(String account, String name, String state, String pageNo) {
        Map<String, Object> map = new HashMap<String, Object>();
        //获取跳转页码，设置分页
        int pageno = pageNo == null || "".equals(pageNo) ? 1 : Integer.valueOf(pageNo);
        Page<User> page = PageHelper.startPage(pageno,8,true);
        //设置查询条件
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(null != account && !"".equals(account)){
            criteria.andLike("account","%"+account+"%");
        }
        if(null != name && !"".equals(name)){
            criteria.andLike("name","%"+name+"%");
        }
        if(null != state && !"".equals(state)){
            criteria.andEqualTo("state",state);
        }
        //管理员可以查询到所有员工，但部门经理只能查询本部门的员工，所以要根据部门id来获取本部门的职位id
        Long deptId = UserContext.getCurrentUser().getRole().getDeptId();
        if(null != deptId && 0 != deptId){
            List<Long> roleIds = roleMapper.selectSimilarRole(deptId);
            criteria.andIn("roleId",roleIds);
        }
        List<User> users = userMapper.selectByExample(example);
        //打包回传数据
        map.put("dataList",users);
        map.put("totalPage",page.getPages());
        map.put("currentPage",page.getPageNum());
        map.put("account",account);
        map.put("name",name);
        map.put("state",state);
        return map;
    }

    @Override
    @Transactional
    public String registUser(User user) {
        user.setCreateTime(new Date());
        user.setAvatar("/img/avatar/timg.jpg");
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("account",user.getAccount());
        List<User> oldUser = userMapper.selectByExample(example);
        if(oldUser.size() > 0){
            return "202";
        }
        int result = userMapper.insertSelective(user);
        if(result != 1){
            return "201";
        }
        Salary salary = new Salary(user.getId());
        int salaryResult = salaryMapper.insertSelective(salary);
        if(result == 1){
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    public List<Role> getRoleList() {
        Long roleId = UserContext.getCurrentUser().getRoleId();
        Example example = new Example(Role.class);
        if(4 == roleId){
            example.createCriteria().andLike("name","%职员%");
        }
        else if(5 == roleId){
            example.createCriteria().andLike("name","%主管%");
        }
        else if(1 == roleId){
            example.createCriteria().andLike("name","%%");
        }
        else {
            return null;
        }
        List<Role> roleList = roleMapper.selectByExample(example);
        return roleList;
    }

    @Override
    @Transactional
    public String updateUserState(User user) {
        int result = userMapper.updateByPrimaryKeySelective(user);
        if(1 == result){
            return "200";
        }
        else {
            return "201";
        }
    }

    @Override
    @Transactional
    public String updateUser(User user) {
        int result = userMapper.updateByPrimaryKeySelective(user);
        if(1 == result){
            User newUser = userMapper.selectOne(user);
            newUser.setRole(roleMapper.selectByPrimaryKey(newUser.getRoleId()));
            UserContext.setCurrentUser(newUser);
            return "200";
        }
        else{
            return "201";
        }
    }

    @Override
    @Transactional
    public String updateImg(String url) {
        User user = UserContext.getCurrentUser();
        user.setAvatar(url);
        int result = userMapper.updateByPrimaryKey(user);
        if(1 == result){
            UserContext.setCurrentUser(user);
            return "200";
        }
        else{
            return "201";
        }
    }

    @Override
    @Transactional
    public String updatePwd(String oldPwd, String newPwd) {
        User user = UserContext.getCurrentUser();
        if(!oldPwd.equals(user.getPassword())){
            return "202";
        }
        user.setPassword(newPwd);
        int result = userMapper.updateByPrimaryKey(user);
        if(1 == result){
            UserContext.setCurrentUser(user);
            return "200";
        }
        else{
            return "201";
        }
    }

    @Override
    public String login(String account, String password) {
        User user = new User();
        //将前端参数保存到查询对象中
        user.setAccount(account);
        user.setPassword(password);
        //验证查询对象是否存在
        User result = userMapper.selectOne(user);
        if(null == result){
            return "201";   //用户名或密码错误
        }
        else {
            result.setRole(roleMapper.selectByPrimaryKey(result.getRoleId()));
            UserContext.setCurrentUser(result);
            return "200";   //账号信息正确
        }
    }

    @Override
    public Map<Long, ParentItem> getMenuList(Long roleId) {
        //根据用户角色获取相应的菜单id集合
        List<Long> menuIds = roleToMenuMapper.selectMenusByRole(roleId);
        Example example = new Example(Menu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",menuIds);
        //根据id查询出menu
        List<Menu> menus = menuMapper.selectByExample(example);
        MenuModel menuModel = new MenuModel(menus);
        Map<Long, ParentItem> menuList = menuModel.getMenuList();

        return menuList;
    }

    @Override
    public User queryById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
