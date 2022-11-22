package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.OrdersDto;
import com.lch.reggie.entity.*;
import com.lch.reggie.mapper.OrdersMapper;
import com.lch.reggie.service.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/13
 * Description:
 */

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public R<String> saveOrder(Long id, Orders orders) {
        //设置好order中需要的信息
        //IdWork可以帮助我们设置订单号
        long id1 = IdWorker.getId();
        String orderId = String.valueOf(id1);
        orders.setNumber(orderId);
        orders.setUserId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        //将购物车中属于该用户的菜品查出
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,id);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0){
            return R.error("购物车内为空，无法下单");
        }
        //将用户中属于该用户的记录查出
        User user = userService.getById(id);
        //将地址表中属于该用户的地址查出
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //将购物车中需要的总金额算出
        BigDecimal count = new BigDecimal(0);
        //在遍历购物车时，顺便也将订单明细完成
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart:shoppingCarts){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setOrderId(id1);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetails.add(orderDetail);
            //金额乘数量
            count = count.add(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())));
        }
        orders.setAmount(count);
        //将用户表中需要的电话和名字取出
        orders.setPhone(user.getPhone());
        orders.setUserName(user.getName());
        //将地址表中需要的收货人和地址信息取出
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //保存order
        this.save(orders);
        //保存order明细
        orderDetailService.saveBatch(orderDetails);
        //付款后可以删除购物车了
        shoppingCartService.remove(queryWrapper);
        return R.success("下单成功");
    }

    @Override
    @Transactional
    public Page<OrdersDto> getOrder(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(ordersPage,queryWrapper);
        //拷贝除了数据的所有属性
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> orders = ordersPage.getRecords();
        List<OrdersDto> ordersDtos = new ArrayList<>();
        //将数据整理后放入Dto中
        for (Orders order:orders){
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order,ordersDto);
            Long orderId = Long.parseLong(order.getNumber());
            Long addressId = order.getAddressBookId();
            Long userId = order.getUserId();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1);
            User user = userService.getById(userId);
            AddressBook addressBook = addressBookService.getById(addressId);
            ordersDto.setUserName(user.getName());
            ordersDto.setPhone(user.getPhone());
            ordersDto.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
            ordersDto.setConsignee(addressBook.getConsignee());
            ordersDto.setOrderDetails(orderDetails);
            ordersDtos.add(ordersDto);
        }
        ordersDtoPage.setRecords(ordersDtos);
        return ordersDtoPage;
    }
}
