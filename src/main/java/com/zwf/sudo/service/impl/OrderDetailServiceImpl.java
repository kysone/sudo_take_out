package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.entity.OrderDetail;
import com.zwf.sudo.mapper.OrderDetailMapper;
import com.zwf.sudo.service.OrderDetailService;
import org.springframework.stereotype.Service;
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
