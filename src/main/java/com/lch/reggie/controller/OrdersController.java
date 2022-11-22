package com.lch.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.OrdersDto;
import com.lch.reggie.entity.Orders;
import com.lch.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lch
 * @create 2022/11/13
 * Description:
 */

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> saveOrder(HttpServletRequest request, @RequestBody Orders order){
        //在结账时，先将用户id查出，这样可以将order中的user_id填好，之后可以根据id去查购物车，将购物车中需要的数据给到order中
        //之后还要查user表，将用户名字查出
        //之后还要查address表，将收货人以及收货地址查出
        Long userId = (Long) request.getSession().getAttribute("user");
        ordersService.saveOrder(userId,order);
        return null;
    }

    //首页头像里面的分页展示
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> pageOrder(int page,int pageSize){
        //需要将OrderDto中的要素填满，一个是用户名字，用户手机号，用户地址，用户收件人，订单详情
        //需要查user表、address_book表、order_detail表、orders表
        Page<OrdersDto> ordersDtoPage = ordersService.getOrder(page, pageSize);
        return R.success(ordersDtoPage);
    }
}
