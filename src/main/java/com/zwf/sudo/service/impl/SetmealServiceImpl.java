package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.common.CustomException;
import com.zwf.sudo.dto.SetmealDto;
import com.zwf.sudo.entity.Setmeal;
import com.zwf.sudo.entity.SetmealDish;
import com.zwf.sudo.mapper.SetmealMapper;
import com.zwf.sudo.service.SetmealDishService;
import com.zwf.sudo.service.SetmealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Resource
    private SetmealDishService setmealDishService;

    @Transactional    //引导类要开启业务
    public void saveWithDish(SetmealDto setmealDto) {
        //保存后setmealDao就生成了套餐id
      this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //加工list，每个套餐关系设置前面生成的套餐id，其他参数都在表单传来了，或有默认值
        setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
      setmealDishService.saveBatch(setmealDishes);

    }

    @Override
        @Transactional
        public void deleteWithDish(List<Long> ids) {
            //查询套餐状态，确定是否可用删除
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.in(Setmeal::getId,ids);
            queryWrapper.eq(Setmeal::getStatus,1);



            //查询选中的并且在启售中的套餐数量
            int count = (int) this.count(queryWrapper);
            if(count > 0){
                //count>0，说明有启售的套餐，全部不允许删除
                //如果不能删除，抛出一个业务异常
                throw new CustomException("套餐正在售卖中，不能删除");
            }

            //如果可以删除，先删除套餐表中的数据---setmeal
            this.removeByIds(ids);

            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
            //删除关系表中的数据----setmeal_dish
            setmealDishService.remove(lambdaQueryWrapper);
        }


 }
