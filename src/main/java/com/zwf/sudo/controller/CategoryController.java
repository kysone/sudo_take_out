package com.zwf.sudo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwf.sudo.common.R;
import com.zwf.sudo.entity.Category;
import com.zwf.sudo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Resource
    private CategoryService categoryService;


    //新增菜品分类
    @PostMapping
    public R<String> save(@RequestBody Category category){

        log.info("category：{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");

    }

    //分页功能
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        Page<Category> pageInfo =new Page<>();

        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);

    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(Long ids){

        log.info("删除的ids为:{}",ids);

        categoryService.remove(ids);

        return R.success("删除成功");

    }

    @PutMapping
    public R<String> updata(@RequestBody Category category){

       if (categoryService.updateById(category)){
           return R.success("修改成功");
       }else {
           return R.error("修改失败");
       }

    }

    @GetMapping("/list")
    //其实参数传Integer的type也可以，只是要再创建分类对象赋值type，麻烦。这里封装成category。
    //参数不是json转实体类，所以不用加@RequestBody
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);

    }
}
