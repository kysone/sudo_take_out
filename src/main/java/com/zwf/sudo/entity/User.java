package com.zwf.sudo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;


@Data
public class User implements Serializable {
    private static final Long serialVersionUID = 2L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private String avatar;
    private Integer status;
}





