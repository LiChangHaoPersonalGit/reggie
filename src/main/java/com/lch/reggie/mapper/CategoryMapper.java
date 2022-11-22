package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/11/4
 * Description:
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
