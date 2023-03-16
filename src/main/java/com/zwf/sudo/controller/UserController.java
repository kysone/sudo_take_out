package com.zwf.sudo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zwf.sudo.common.R;
import com.zwf.sudo.entity.User;
import com.zwf.sudo.service.UserService;
import com.zwf.sudo.service.impl.UserServiceImpl;
import com.zwf.sudo.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String MyFrom;

    @Resource
    private UserService userService;
  /*  //获取验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user){
        //获取邮箱号
        //相当于发送短信定义的String to
        String email = user.getPhone();
        String subject = "瑞吉外卖";
        //StringUtils.isNotEmpty字符串非空判断
        if (StringUtils.isNotEmpty(email)) {
            //发送一个四位数的验证码,把验证码变成String类型
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String text = "【瑞吉外卖】您好，您的登录验证码为：" + code + "，请尽快登录";
            log.info("验证码为：" + code);
            //发送短信
            userService.sendMsg(email,subject,text);
            //将验证码保存到session当中
            session.setAttribute(email,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送异常，请重新发送");
    }
    //登录*/


    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,
                             HttpSession session){
        log.info("R<String> sendMsg()进来了");
        //获取手机号
        final String phone = user.getPhone();//就不修改实体类了把邮件写进 Phone

        //判断手机号不为空
        if(StringUtils.isNotEmpty(phone)) {
            //生成4位验证码
            final String code =
                    ValidateCodeUtils.generateValidateCode(4).toString();
            System.out.println("==========");
            System.out.println(code);//验证码
            System.out.println("==========");
            //***************************************************/
            //创建简单邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MyFrom);//用自己的邮件发
            //谁要接收
            message.setTo(phone);
            //邮件标题
            message.setSubject("验证码");
            //邮件内容
            message.setText(code);//
            try {
                mailSender.send(message);
                //需要保存一下验证码，后面用来验证
                session.setAttribute(phone, code);
                return R.success("发送成功");
            } catch (MailException e) {
                e.printStackTrace();
                return R.error("短信发送失败");
            }
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    //Map存JSON数据
    public R<User> login(HttpSession session,@RequestBody Map map){
        //获取邮箱，用户输入的
        String phone = map.get("phone").toString();
        //获取验证码，用户输入的
        String code = map.get("code").toString();
        //获取session中保存的验证码
        Object sessionCode = session.getAttribute(phone);
        //如果session的验证码和用户输入的验证码进行比对,&&同时
        if (sessionCode != null && sessionCode.equals(code)) {
            //要是User数据库没有这个邮箱则自动注册,先看看输入的邮箱是否存在数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            //获得唯一的用户，因为手机号是唯一的
            User user = userService.getOne(queryWrapper);
            //要是User数据库没有这个邮箱则自动注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                //取邮箱的前五位为用户名
                user.setName(phone.substring(0,6));
                userService.save(user);
            }
            //不保存这个用户名就登不上去，因为过滤器需要得到这个user才能放行，程序才知道你登录了
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}