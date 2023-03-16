package com.zwf.sudo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwf.sudo.entity.Orders;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
