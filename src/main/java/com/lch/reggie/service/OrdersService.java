package com.lch.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.OrdersDto;
import com.lch.reggie.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lch
 * @create 2022/11/13
 * Description:
 */

public interface OrdersService extends IService<Orders> {

    public R<String> saveOrder(Long id,Orders orders);

    public Page<OrdersDto> getOrder(int page,int pageSize);
}
