package com.faiop.core.mapper;

import com.faiop.core.pojo.User;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description:
 * @Author RM
 */
public interface UserMapper extends Mapper<User> {

    @Select("select id from t_user where name like '%${name}%'")
    List<Long> getIdByName(@Param("name") String name);
}
