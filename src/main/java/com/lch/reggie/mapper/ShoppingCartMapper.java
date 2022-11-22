package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/11/10
 * Description:购物车mapper类
 */

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
