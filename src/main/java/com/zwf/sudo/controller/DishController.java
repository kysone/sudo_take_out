package com.zwf.sudo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwf.sudo.common.R;
import com.zwf.sudo.dto.DishDto;
import com.zwf.sudo.entity.Category;
import com.zwf.sudo.entity.Dish;
import com.zwf.sudo.entity.DishFlavor;
import com.zwf.sudo.service.CategoryService;
import com.zwf.sudo.service.DishFlavorService;
import com.zwf.sudo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private RedisTemplate redisTemplate;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品管理分页
     * */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询，dishService查的是dish表，所以必须先Dish查询，再拷贝到DishDto
        dishService.page(pageInfo,queryWrapper);

        //注意这一步，将Dish的page拷贝到DishDto的page，第三个参数是忽略records属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        //steam流的map（Lambda表达式）对list进行加工，stream流返回值是可以换泛型的。等号左边是List<DishDto>，右边是List<Dish>
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //对象拷贝的工具类BeanUtils，将list里每一个菜品Dish对象信息拷贝到DishDto对象
            BeanUtils.copyProperties(item,dishDto);
            //将菜品所属分类的id查询为name，存到DishDto对象的categoryName，再返回替换item，完成item的加工
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     *根据id查找对象
     * */

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
       DishDto dishDto=dishService.getByIdWithFlavor(id);
       return R.success(dishDto);
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }



  /*  @GetMapping("/list")
    //根据条件查询多菜品。如果参数只有categoryId，那就只能查单一条件，通用性低
    public R<List<Dish>> list(Dish dish) {
        log.info("dish:{}", dish);
        //条件构造器，条件name和categoryId和status（price那些条件始终没需求就不做条件），根据更新时间排序
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.like(StringUtils.isNotEmpty(dish.getName()), Dish::getName, dish.getName());
        queryWrapper.eq(null != dish.getCategoryId(), Dish::getCategoryId, dish.getCategoryId());
        //查询条件，状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //条件查询到菜品列表
        List<Dish> list = dishService.list(queryWrapper);
        //对list每个元素加工，设置分类和味道信息，返回成dto，
      *//*  List<DishDto> dishDtos = dishs.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());

            dishDto.setFlavors(dishFlavorService.list(wrapper));
            return dishDto;
        }).collect(Collectors.toList());*//*

        return R.success(list);
    }
*/

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        List<DishDto> dishDtoList=null;

        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();

        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList!=null){
            return R.success(dishDtoList);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

            dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

            redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
