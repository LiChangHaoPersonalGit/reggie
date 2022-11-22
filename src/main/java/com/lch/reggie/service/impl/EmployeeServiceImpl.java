package com.lch.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.reggie.entity.Employee;
import com.lch.reggie.mapper.EmployeeMapper;
import com.lch.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author lch
 * @create 2022/10/29
 * Description:Service层实现类
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
}
