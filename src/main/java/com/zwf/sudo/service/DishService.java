package com.zwf.sudo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwf.sudo.dto.DishDto;
import com.zwf.sudo.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
