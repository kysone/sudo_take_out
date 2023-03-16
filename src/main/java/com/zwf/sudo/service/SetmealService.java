package com.zwf.sudo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwf.sudo.dto.SetmealDto;
import com.zwf.sudo.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWithDish(List<Long> ids);
}
