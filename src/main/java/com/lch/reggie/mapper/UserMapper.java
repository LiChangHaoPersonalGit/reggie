package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/11/9
 * Description:
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
