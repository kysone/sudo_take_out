package com.zwf.sudo.dto;

import com.zwf.sudo.entity.SetmealDish;
import com.zwf.sudo.entity.Setmeal;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    //套餐和菜品关系的list
    private List<SetmealDish> setmealDishes;
    //所属分类名
    private String categoryName;
}