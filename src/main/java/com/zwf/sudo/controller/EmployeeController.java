package com.zwf.sudo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zwf.sudo.common.R;
import com.zwf.sudo.entity.Employee;
import com.zwf.sudo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1.将页面传来的数据进行md5加密，同一个密码多次被md5加密是一样的
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名称查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果查询没有则返回失败
        if (emp == null) {
            return R.error("用户或者密码错误");
        }

        //4.密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5.员工状态比对
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

        //6.登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }


    /**
    *退出登录
     */
    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        try {
            request.getSession().removeAttribute("employee");
        } catch (Exception e) {
            return R.error("登出失败");

        }
        return R.success("登出成功");

    }

    /**
     * 新增员工信息
     * */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息{}",employee.toString());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());

        //employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        employeeService.save(employee);

        return R.success("新增成功");

    }


    /**
     * 分页管理
     * */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        Page pageInfo=new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        queryWrapper.orderByDesc(Employee::getUpdateUser);

        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);


    }


    /**
     * 修改用户权限
     * */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long id=Thread.currentThread().getId();
        log.info("线程id{}",id);

        //Long empid= (Long) request.getSession().getAttribute("employee");


        //employee.setUpdateTime(LocalDateTime.now());

        //employee.setUpdateUser(empid);

        employeeService.updateById(employee);

        return R.success("员工修改信息成功");
    }



    /**
     * 修改用户信息
     * */
    @GetMapping("/{id}")
    public R<Employee> getByid(@PathVariable Long id){

        Employee employee = employeeService.getById(id);



        log.info("查询到的员工{}",employee);

        if (employee!=null){
            return R.success(employee);
        }

        else{
            return R.error("找不到该员工");
        }


    }



}
