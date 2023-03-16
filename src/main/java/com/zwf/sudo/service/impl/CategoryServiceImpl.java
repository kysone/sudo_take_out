package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.common.CustomException;
import com.zwf.sudo.entity.Category;
import com.zwf.sudo.entity.Dish;
import com.zwf.sudo.entity.Setmeal;
import com.zwf.sudo.mapper.CategoryMapper;
import com.zwf.sudo.service.CategoryService;
import com.zwf.sudo.service.DishService;
import com.zwf.sudo.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private SetmealService setmealService;

    @Resource
    private DishService dishService;


    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper1=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper1.eq(Dish::getCategoryId,id);

        long count1 = dishService.count(dishLambdaQueryWrapper1);

        if (count1>0){

            throw new CustomException("当前分类下关联了菜品，不能删除");

    }

        LambdaQueryWrapper<Setmeal> dishLambdaQueryWrapper2=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper2.eq(Setmeal::getCategoryId,id);

        long count2 = setmealService.count(dishLambdaQueryWrapper2);

        if (count2>0){

            throw new CustomException("当前分类下关联了套餐，不能删除");

        }

        super.removeById(id);






    }
}
