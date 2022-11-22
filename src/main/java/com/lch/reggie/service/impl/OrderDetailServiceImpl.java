package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.OrderDetail;
import com.lch.reggie.mapper.OrderDetailMapper;
import com.lch.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/13
 * Description:
 */

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
