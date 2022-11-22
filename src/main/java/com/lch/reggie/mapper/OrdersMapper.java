package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/11/13
 * Description:
 */

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
