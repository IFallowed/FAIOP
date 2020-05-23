package com.faiop.core.mapper;

import com.faiop.core.pojo.Role;
import com.faiop.core.pojo.RoleToMenu;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description:
 * @Author RM
 */
public interface RoleToMenuMapper extends Mapper<RoleToMenu> {

    @Select("select menuId from t_roleToMenu where roleId = #{key}")
    List<Long> selectMenusByRole(Long roleId);
}
