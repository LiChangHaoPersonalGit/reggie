package com.lch.reggie.controller;

import ch.qos.logback.core.pattern.ConverterUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.reggie.common.R;
import com.lch.reggie.dto.SetmealDto;
import com.lch.reggie.entity.Category;
import com.lch.reggie.entity.Setmeal;
import com.lch.reggie.entity.SetmealDish;
import com.lch.reggie.service.CategoryService;
import com.lch.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/7
 * Description:
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    //添加套餐
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.addSetmealWithDish(setmealDto);
        return R.success("添加成功");
    }

    //分页查询套餐
    @GetMapping("/page")
    @Transactional
    public R<Page<SetmealDto>> getSetmeal(@PathParam("page") int page,
                                       @PathParam("pageSize") int pageSize,
                                       @PathParam("name") String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        queryWrapper.like(name != null,Setmeal::getName,name);
        setmealService.page(setmealPage,queryWrapper);
        //将page中除了数据的信息进行拷贝
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> setmeals = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal:setmeals){
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            //将setmeal的其他信息拷贝给setmealDDto
            BeanUtils.copyProperties(setmeal,setmealDto);
            //为setmealDto增加categoryName
            setmealDto.setCategoryName(category.getName());
            setmealDtos.add(setmealDto);
        }
        setmealDtoPage.setRecords(setmealDtos);
        return R.success(setmealDtoPage);
    }

    //回显更新数据
    @GetMapping("/{id}")
    public R<SetmealDto> updateSetmeal(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.updateSetmealWithDish(id);
        return R.success(setmealDto);
    }

    //保存更新数据
    @PutMapping
    public R<String> saveUpdateSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("修改成功");
    }

    //删除套餐
    @DeleteMapping
    //此处用@PathParam无法用List<Long>接受的原因是，PathParam是将路径中的参数和值直接截取出来
    //基本拿到的都是字符串，然后通过SpringMvc的解析器完成不同类型的转换
    //而使用@RequestParam使用的是Request中的参数，此时前端有请求拦截器，将参数处理好了，所以使用@RequestParam就可以用List接收
    public R<String> delSetmeal(@RequestParam("ids") List<Long> ids){
        setmealService.delSetmealWithDish(ids);
        return R.success("删除成功");
    }

    //更新套餐售卖状态
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        //更新套餐的售卖状态必须先检查自己的菜品是否都在售，如果有停售的则无法起售，停售的话不影响
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        if (status == 0){
            setmealService.update(setmeal,queryWrapper);
        } else {
            setmealService.checkDishStatus(ids);
            setmealService.update(setmeal,queryWrapper);
        }
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);
    }
}
//SELECT deptno,AVG(sal) FROM emp GROUP BY deptno HAVING AVG(sal) > 2000