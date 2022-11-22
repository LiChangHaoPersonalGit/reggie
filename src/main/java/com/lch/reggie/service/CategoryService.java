package com.lch.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.Category;

import java.util.List;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

public interface CategoryService extends IService<Category> {

    public R<String> removeCategory(List<Long> ids);
}
