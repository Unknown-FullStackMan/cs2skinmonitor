package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.ProcessedOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 15:11
 * @Description
 */
@Mapper
public interface ProcessedOrderMapper extends BaseMapper<ProcessedOrder> {

    @Select("select * from processed_order where timestamp = #{timestamp}")
    ProcessedOrder selectByDate(Long timestamp);
}
