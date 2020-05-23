package com.faiop.core.mapper;

import com.faiop.core.pojo.Role;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description:
 * @Author RM
 */
public interface RoleMapper extends Mapper<Role> {
    @Select("select id from t_role where deptId = #{key}")
    List<Long> selectSimilarRole(Long deptId);
}
