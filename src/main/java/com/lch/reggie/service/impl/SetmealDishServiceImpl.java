package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.SetmealDish;
import com.lch.reggie.mapper.SetmealDishMapper;
import com.lch.reggie.mapper.SetmealMapper;
import com.lch.reggie.service.SetmealDishService;
import com.lch.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/7
 * Description:套餐菜品关系Service实现类
 */

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
