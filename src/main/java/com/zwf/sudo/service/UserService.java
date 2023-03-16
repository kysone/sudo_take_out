package com.zwf.sudo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zwf.sudo.entity.User;
import org.springframework.stereotype.Service;



public interface UserService extends IService<User> {
    //发送邮件
  void sendMsg(String to,String subject,String text);


}