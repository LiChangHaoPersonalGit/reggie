package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.Category;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.Setmeal;
import com.lch.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

@Controller
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<Category>> categoryPage(int page,int pageSize){
        Page<Category> categoryPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //第一排序条件(按sort排序)相同时，按照第二排序条件(updateTime排序)
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        categoryService.page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    @DeleteMapping
    public R<String> delCategory(@RequestParam List<Long> ids){
        return categoryService.removeCategory(ids);
    }

    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //菜品添加界面的菜品分类列表
    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //第一排序条件(按sort排序)相同时，按照第二排序条件(updateTime排序)
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
