package com.zwf.sudo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwf.sudo.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
