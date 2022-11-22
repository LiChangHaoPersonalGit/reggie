package com.lch.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author lch
 * @create 2022/10/29
 * Description:员工实体类的Mapper接口类
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
