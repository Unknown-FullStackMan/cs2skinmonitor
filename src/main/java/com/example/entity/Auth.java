package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author Simple.Mu
 * @Date 2025/3/23 17:40
 * @Description
 */
@Data
@TableName("auth")
public class Auth {


    @TableId(type = IdType.INPUT)
    private Integer id;

    private String platform;

    private String authorization;
}
