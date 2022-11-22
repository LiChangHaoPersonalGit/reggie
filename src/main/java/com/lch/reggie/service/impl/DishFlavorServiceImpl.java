package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.DishFlavor;
import com.lch.reggie.mapper.DishFlavorMapper;
import com.lch.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/7
 * Description:DishFlavor的Service实现类
 */

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
