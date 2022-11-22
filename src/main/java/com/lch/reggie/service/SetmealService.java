package com.lch.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.reggie.dto.SetmealDto;
import com.lch.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author lch
 * @create 2022/11/4
 * Description:套餐Service层
 */

public interface SetmealService extends IService<Setmeal> {

    public void addSetmealWithDish(SetmealDto setmealDto);

    public SetmealDto updateSetmealWithDish(Long id);

    public void saveSetmealWithDish(SetmealDto setmealDto);

    public void delSetmealWithDish(List<Long> ids);

    public void checkDishStatus(List<Long> ids);
}
