package com.zwf.sudo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.entity.ShoppingCart;
import com.zwf.sudo.mapper.ShoppingCartMapper;
import com.zwf.sudo.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
