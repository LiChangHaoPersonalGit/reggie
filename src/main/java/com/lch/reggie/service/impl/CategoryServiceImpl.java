package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.common.CustomException;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.Category;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.Setmeal;
import com.lch.reggie.mapper.CategoryMapper;
import com.lch.reggie.service.CategoryService;
import com.lch.reggie.service.DishService;
import com.lch.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public R<String> removeCategory(List<Long> ids) {
        //批量删除时，有任何一个分类有关联菜品或套餐时，直接全部不给删除
        for (Long id : ids) {
            LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
            dishQueryWrapper.eq(Dish::getCategoryId,id);
            LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
            setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
            Long dish = dishService.count(dishQueryWrapper);
            if (dish > 0){
                throw new CustomException("Dish Exist,Can't Delete Category");
            }
            Long setmeal = setmealService.count(setmealQueryWrapper);
            if (setmeal > 0){
                throw new CustomException("Setmeal Exist,Can't Delete Category");
            }
        }
        removeByIds(ids);
        return R.success("删除成功");
    }
}
