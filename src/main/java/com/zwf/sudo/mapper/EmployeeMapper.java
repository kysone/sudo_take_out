package com.zwf.sudo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zwf.sudo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
