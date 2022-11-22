package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.AddressBook;
import com.lch.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/10
 * Description:收货地址类Controller
 */

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //获取默认的地址，即is_default字段为1的地址
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(HttpServletRequest request){
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook address = addressBookService.getOne(queryWrapper);
        if (address == null){
//            return R.error("请先添加地址");
            return R.success(new AddressBook());
        }
        return R.success(address);
    }

    @GetMapping("/list")
    public R<List<AddressBook>> getAddressList(HttpServletRequest request){
        Long userId = (Long) request.getSession().getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(queryWrapper);
        return R.success(addressBooks);
    }

    @PostMapping
    public R<String> saveAddress(@RequestBody AddressBook addressBook, HttpServletRequest request){
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }

    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook){
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置成功");
    }

    @GetMapping("/{id}")
    public R<AddressBook> updateAddress(@PathVariable("id") Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> updateUserAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delAddress(@RequestParam("ids") List<Long> ids){
        addressBookService.removeBatchByIds(ids);
        return R.success("删除成功");
    }
}
