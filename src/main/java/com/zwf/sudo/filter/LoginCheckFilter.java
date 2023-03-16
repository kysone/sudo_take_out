package com.zwf.sudo.filter;


import com.alibaba.fastjson.JSON;
import com.zwf.sudo.common.BaseContext;
import com.zwf.sudo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符，别忘了
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;


        //1.拦截请求
        String requestURI=request.getRequestURI();
        log.info("拦截到请求{}",requestURI);



        //定义不需要拦截的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                 "/user/sendMsg",
                "/user/login",


                // 发送登录验证码
        };

        //2.判断本次请求是否要处理
        boolean check=check(urls,requestURI);

        //3.如果不处理，则放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            Long id=Thread.currentThread().getId();
            log.info("线程id={}",id);


           Long empId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4.判断移动端用户是否登陆
        final Object user = request.getSession().getAttribute("user");
        if(user!= null){
            //插入基于ThreadLocal封装的工具类
            final Long userId
                    = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);//绑定当前id
            //放行
            filterChain.doFilter(request,response);
            return;
        }



        log.info("用户未登录");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        //必须错误信息NOTLOGIN，因为前端根据msg==“NOTLOGIN”和code==0判断未登录
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;








    }

    public boolean check(String[] urls,String requestURI){
        for (String url:urls){
            boolean match=PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
