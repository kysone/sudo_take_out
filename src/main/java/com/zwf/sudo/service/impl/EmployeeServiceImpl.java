package com.zwf.sudo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zwf.sudo.entity.Employee;
import com.zwf.sudo.mapper.EmployeeMapper;
import com.zwf.sudo.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
