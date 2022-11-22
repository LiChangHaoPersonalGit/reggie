package com.lch.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lch
 * @create 2022/10/29
 * Description:员工实体类
 */

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String username;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //该注解是表示该字段为公共字段
    //何为公共字段，即出现在多表中的相同字段，MybatisPlus为了减少代码量，提供了可以给这些字段统一赋值的方法，但是要先在字段上加注解
    //INSERT表示创建时插入，只一次
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //INSERT_UPDATE表示在创建和更新时插入，可多次
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
