package com.zwf.sudo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zwf.sudo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
