package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.dto.DishDto;
import com.zwf.sudo.entity.Dish;
import com.zwf.sudo.entity.DishFlavor;
import com.zwf.sudo.mapper.DishMapper;
import com.zwf.sudo.service.DishFlavorService;
import com.zwf.sudo.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Resource
    private DishFlavorService dishFlavorService;



    @Transactional
    public void saveWithFlavor(DishDto dishDto){

        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
//        stream流的filter是满足条件的留下，是对原数组的过滤；map则是对原list的加工，map里是Lambda表达式，形容对list加工。
//        通过stream流的map将list加工，把这个菜品的id加到每个list元素的dishId属性；
        flavors = flavors.stream().map((item) -> {    //给list每个元素map加工，返回值为加工后的结果
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(flavors);
    }

    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

}
