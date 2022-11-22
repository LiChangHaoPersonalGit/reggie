package com.lch.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.DishDto;
import com.lch.reggie.entity.Dish;

import java.util.List;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public Page<DishDto> getWithCategory(int page, int pageSize, String name);

    public DishDto updateGetFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public List<DishDto> getDishWithFlavor(DishDto dishDto);
}
