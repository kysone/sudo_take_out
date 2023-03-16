package com.zwf.sudo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    //声明序列化的版本
    private static final long serialVersionUID = 1L;

    //员工表的各个属性
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill =FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //阿里巴巴的开发规范中推荐每个表都带有一个createTime 和一个 updateTime， 但是每次自己手动添加太麻烦了，可以配置MP让其自动添加，使用@TableField的fill注解。
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long updateUser;



}
