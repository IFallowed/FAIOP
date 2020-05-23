package com.faiop.core.mapper;

import com.faiop.core.pojo.ContractRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Description:
 * @Author RM
 */
public interface ContractRecordMapper extends Mapper<ContractRecord> {
    @Select("select scId from t_scRecord where state = #{state}")
    List<Long> getscIdByState(@Param("state") String state);
}
