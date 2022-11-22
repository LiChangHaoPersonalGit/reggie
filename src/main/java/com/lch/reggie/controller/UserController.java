package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.User;
import com.lch.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author lch
 * @create 2022/11/9
 * Description:
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    private R<String> login(HttpServletRequest request, @RequestBody User user){
        //登录的情况有两种：
        //当我们的表中有该用户时，说明是老用户，如果用户没被禁用时，直接登录进去就行
        //当我们的表中没有该用户时，说明是新用户，我们应该直接帮他注册以后再进行登录
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,user.getPhone());
        //判断是否有该用户
        if (userService.count(queryWrapper) != 0){
            //有的话判断是否被禁用
            User one = userService.getOne(queryWrapper);
            if (one.getStatus() == 0){
                return R.error("用户被禁用");
            }
            //没被禁用就将查出来的旧用户的id存入session中并登录
            request.getSession().setAttribute("user",one.getId());
        } else {
            //新用户的话帮其注册，注册后会自动返回主键即id，将其存入session中并登录
            userService.save(user);
            request.getSession().setAttribute("user",user.getId());
        }
        return R.success("登陆成功");
    }
}
