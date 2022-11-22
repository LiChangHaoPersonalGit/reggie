package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.User;
import com.lch.reggie.mapper.UserMapper;
import com.lch.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/9
 * Description:
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
