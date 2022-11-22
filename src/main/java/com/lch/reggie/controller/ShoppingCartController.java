package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.ShoppingCart;
import com.lch.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/10
 * Description:购物车Controller层
 */

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> shoppingCartList(){
        List<ShoppingCart> shoppingCarts = shoppingCartService.list();
        return R.success(shoppingCarts);
    }

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart){
        Long userId = (Long) request.getSession().getAttribute("user");
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if (cart != null){
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
            return R.success(cart);
        } else {
            shoppingCartService.save(shoppingCart);
            shoppingCart.setNumber(1);
            return R.success(shoppingCart);
        }
    }

    @PostMapping("/sub")
    public R<ShoppingCart> subShoppingCart(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one.getNumber() == 1){
            shoppingCartService.removeById(one);
            one.setNumber(0);
            return R.success(one);
        } else {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
            return R.success(one);
        }
    }

    @DeleteMapping("/clean")
    public R<String> cleanShpppingCart(){
        shoppingCartService.remove(new LambdaQueryWrapper<>());
        return R.success("清空购物车成功");
    }
}
