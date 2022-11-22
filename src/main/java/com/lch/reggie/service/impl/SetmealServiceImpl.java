package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.common.CustomException;
import com.lch.reggie.dto.SetmealDto;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.Setmeal;
import com.lch.reggie.entity.SetmealDish;
import com.lch.reggie.mapper.SetmealMapper;
import com.lch.reggie.service.DishService;
import com.lch.reggie.service.SetmealDishService;
import com.lch.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lch
 * @create 2022/11/4
 * Description:套餐Service层实现类
 */

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    @Override
    @Transactional
    public void addSetmealWithDish(SetmealDto setmealDto) {
        //保存套餐信息
        this.save(setmealDto);
        Long id = setmealDto.getId();
        //菜品信息缺失套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(id);
        }
        //保存菜品信息
        setmealDishService.saveBatch(setmealDishes);
    }

    //回显更新数据
    @Override
    @Transactional
    public SetmealDto updateSetmealWithDish(Long id) {
        //更新数据包含categoryId、Dish的list、setmeal基本信息
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        queryWrapper.orderByAsc(SetmealDish::getSort).orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    //将修改后的数据保存进数据库
    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long setmealId = setmealDto.getId();
        //由于setmeal_dish表格和setmeal表格不同，其在更新时，可能出现增删套餐中菜品的现象
        //所以考虑先将原属于该套餐的菜品全部删去，再将现有菜品保存进数据库，避免进行两次数据库增删操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealId != null,SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealId);
        }
        //删除再更新
        setmealDishService.remove(queryWrapper);
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除套餐类
    @Override
    @Transactional
    public void delSetmealWithDish(List<Long> ids) {
        //无法删除套餐的情况是当套餐存在在售时不能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //当我们删除套餐时，应该把套餐菜品关联表中的相关数据也删除，这个不需要考虑是否是停售，因为不是删除的菜品表中的数据
        //只是将关联表中的数据删除
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        if (this.count(queryWrapper) == 0){
            this.remove(queryWrapper);
            setmealDishService.remove(queryWrapper1);
        } else {
            throw new CustomException("SETMEALS STATUS ARE ON SALE");
        }
    }

    //套餐起售时，检查其下菜品是否已经停售
    @Override
    @Transactional
    public void checkDishStatus(List<Long> ids) {
        //将要起售的套餐与其关联的菜品一起查出来
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        //将要起售的菜品id获取
        List<Long> dishIds = setmealDishes.stream().map(SetmealDish::getDishId).collect(Collectors.toList());
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Dish::getStatus,0);
        queryWrapper1.in(Dish::getId,dishIds);
        //要是与套餐关联的菜品中，有一个是停售的，那就抛出异常
        //都是起售状态时，则可以起售
        if (dishService.count(queryWrapper1) != 0){
            throw new CustomException("DISHES ASSOCIATE WITH SETMEALS STATUS ARE HALT SALE");
        }
    }
}
