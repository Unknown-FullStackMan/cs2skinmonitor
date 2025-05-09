package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.SkinItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author Simple.Mu
 * @Date 2025/3/22 18:18
 * @Description
 */
@Mapper
public interface SkinItemMapper extends BaseMapper<SkinItem> {

    @Select("select * from skin_item where name = #{name}")
    SkinItem selectBySkinName(String name);


    @Select("select * from skin_item where name = #{name} and abrade = #{abrade}")
    SkinItem selectBySkinNameAndAbrade(String name,String abrade);



    @Insert({
            "<script>",
            "INSERT INTO skin_item (name, abrade,merge,purchase_price,quantity,purchase_avg_price) VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "( #{item.name}, #{item.abrade},#{item.merge},#{item.purchasePrice},#{item.quantity},#{item.purchaseAvgPrice} )",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("list") List<SkinItem> skinItems);
}
