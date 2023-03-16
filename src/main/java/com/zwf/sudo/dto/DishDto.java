package com.zwf.sudo.dto;


import com.zwf.sudo.entity.Dish;
import com.zwf.sudo.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();
    //下面两个成员变量暂时用不到，先放着，后面有其他表单需要封装

    //菜品所属分类名称
    private String categoryName;

    private Integer copies;
}