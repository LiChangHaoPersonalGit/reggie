package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.reggie.common.CustomException;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.DishDto;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.DishFlavor;
import com.lch.reggie.entity.Setmeal;
import com.lch.reggie.entity.SetmealDish;
import com.lch.reggie.service.DishFlavorService;
import com.lch.reggie.service.DishService;
import com.lch.reggie.service.SetmealDishService;
import com.lch.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    public R<Page<DishDto>> dishPage(int page,int pageSize,String name){
        Page<DishDto> dishDtoPage = dishService.getWithCategory(page, pageSize, name);
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        //已被DTO类代替
        //List<DishFlavor> flavors = dish.getFlavors();
        //dish.setCode("0");
        //dishService.save(dish);
        //for (DishFlavor flavor:flavors){
            //flavor.setDishId(dish.getId());
        //}
        //dishFlavorService.saveBatch(flavors);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getUpdateDish(@PathVariable("id") Long id){
        log.info(id.toString());
        DishDto dishDto = dishService.updateGetFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    //添加套餐界面的添加菜品所用到的请求
    @GetMapping("/list")
    public R<List<DishDto>> getDishList(DishDto dishDto){
        List<DishDto> dishDtos = dishService.getDishWithFlavor(dishDto);
        return R.success(dishDtos);
    }

    @PostMapping("/status/{status}")
    @Transactional
    public R<String> updateStatus(@PathVariable("status") Integer status,@RequestParam("ids") List<Long> ids){
        //当我们停售菜品时，要同时停售与它关联的套餐，当起售菜品时，则直接起售即可
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        Dish dish = new Dish();
        dish.setStatus(status);
        if (status == 1){
            dishService.update(dish,queryWrapper);
        } else {
            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(SetmealDish::getDishId,ids);
            //查出来后，要判断一下是否为空列表，如果不是空列表，说明要停售的菜品根本没有绑定套餐
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper1);
            if (setmealDishes.size() != 0){
                //确实绑定了套餐时，先把套餐的id取出来
                List<Long> setmealIds = setmealDishes.stream().map(SetmealDish::getSetmealId).distinct().collect(Collectors.toList());
                LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.in(Setmeal::getId,setmealIds);
                Setmeal setmeal = new Setmeal();
                setmeal.setStatus(status);
                //把套餐的状态更新为停售
                setmealService.update(setmeal,queryWrapper2);
            }
            dishService.update(dish,queryWrapper);
        }
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delDish(@RequestParam("ids") List<Long> ids){
        //删除菜品时，如果和套餐绑定，则直接抛出不可删除异常
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getDishId,ids);
        if (setmealDishService.count(queryWrapper) != 0){
            throw new CustomException("Setmeal Exist,Can't Delete Dish");
        }
        dishService.removeBatchByIds(ids);
        return R.success("删除成功");
    }
}
