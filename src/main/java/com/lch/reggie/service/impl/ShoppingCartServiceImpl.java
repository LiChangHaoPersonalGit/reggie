package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.ShoppingCart;
import com.lch.reggie.mapper.ShoppingCartMapper;
import com.lch.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/10
 * Description:购物车类Service实现类
 */

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
