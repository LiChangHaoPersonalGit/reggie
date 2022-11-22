package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/11/10
 * Description:收货地址表Mapper
 */

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
