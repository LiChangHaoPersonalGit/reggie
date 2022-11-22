package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.AddressBook;
import com.lch.reggie.mapper.AddressBookMapper;
import com.lch.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/11/10
 * Description:收货地址表Service实现类
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
