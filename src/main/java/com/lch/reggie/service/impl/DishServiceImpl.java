package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.DishDto;
import com.lch.reggie.entity.Category;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.DishFlavor;
import com.lch.reggie.entity.SetmealDish;
import com.lch.reggie.mapper.DishMapper;
import com.lch.reggie.service.CategoryService;
import com.lch.reggie.service.DishFlavorService;
import com.lch.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Lazy
    @Autowired
    private CategoryService categoryService;

    //新增菜品的同时保存口味数据
    @Override
    //涉及多张表操作时要加上事务
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //当我们保存完之后，dish的id就已经赋值上了
        for (DishFlavor flavor:dishDto.getFlavors()){
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    @Transactional
    public Page<DishDto> getWithCategory(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        if (name != null){
            queryWrapper.like(Dish::getName,name);
        }
        //常规分页查询，并将结果拷贝至泛型为DishDto的page对象
        this.page(dishPage,queryWrapper);
        //准备拷贝对象
        Page<DishDto> dtoPage = new Page<>();
        //利用BeanUtils的copyProperties方法将dishPage中除了Records的其它字段拷过去
        //Records是page中的数据字段，我们正好就是两个page的内容不同，但是其他的属性全都相同，ignoreProperties翻译过来就是忽略的属性
        BeanUtils.copyProperties(dishPage,dtoPage,"records");
        //对Records进行精细处理
        List<Dish> dishes = dishPage.getRecords();
        List<DishDto> dtos = new ArrayList<>();
        for (Dish dish:dishes){
            Category category = categoryService.getById(dish.getCategoryId());
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setCategoryName(category.getName());
            dtos.add(dishDto);
        }
        dtoPage.setRecords(dtos);
        return dtoPage;
    }

    //点击新增按钮时，进行回显数据
    @Override
    @Transactional
    public DishDto updateGetFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //dish的id关乎口味和菜品两种
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        //完成dish的数据拷贝
        BeanUtils.copyProperties(dish,dishDto);
        //添加查出的口味数据
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    //保存修改信息
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        //在修改数据时，我们还可以对口味进行添加和删除，所以我们应该先把所有的口味删除，再将新口味(可能和之前一样)加入，避免了用户删除和添加口味
        //判断这个标签是不是新标签的关键就是，看这个标签有没有dishId，所以先遍历一边将新口味给上dishId
        for (DishFlavor flavor:flavors){
            if (flavor.getDishId() == null) {
                flavor.setDishId(dishDto.getId());
            }
        }
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public List<DishDto> getDishWithFlavor(DishDto dishDto) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dishDto.getCategoryId() != null,Dish::getCategoryId,dishDto.getCategoryId());
        //只有状态为1的才查出来
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(queryWrapper);
        //将dish数据放入dishDto中
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish:dishList){
            DishDto dishDto1 = new DishDto();
            BeanUtils.copyProperties(dish,dishDto1);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dish.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto1.setFlavors(dishFlavors);
            dishDtoList.add(dishDto1);
        }
        return dishDtoList;
    }
}
