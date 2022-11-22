package com.lch.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.reggie.common.R;
import com.lch.reggie.entity.Employee;
import com.lch.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author lch
 * @create 2022/10/29
 * Description:员工实体类Controller层
 */

@Slf4j
@RestController
//因为请求分为前缀和后缀，我们登录界面发送的请求都是/employee/**形式，所以先给上/employee前缀
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //用于响应请求路径为/employee/login的请求，并给浏览器返回通用返回值类型R
    @PostMapping("/login")
    //由于我们需要将成功登录的用户名和密码交由Session保管以便后续操作，所以我们需要设置HttpServletRequest参数
    //由于发送给后端的是JSON格式数据，所以我们需要使用@RequestBody接收
    public R<Employee> login(HttpServletRequest httpRequest, @RequestBody Employee employee){

        //将页面给我们的密码做md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //根据页面提供的用户名进行数据库查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //根据查询条件查询出一个对象，而且getOne方法根据条件查询出来的结果必须是唯一的，因为我们在数据库中定义好了所以是唯一的
        Employee emp = employeeService.getOne(queryWrapper);

        //判断用户是否存在
        if (emp == null){
            //未查到
            return R.error("用户名错误");
        }

        //判断密码是否正确
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //判断员工状态，0为被禁用
        if (emp.getStatus() == 0){
            return R.error("您已被禁止登录");
        }

        //登陆成功，将id放入Session中
        httpRequest.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    //用于响应请求路径为/employee/logout的请求，并给浏览器返回通用返回值类型R
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpRequests){
        //退出时移除session并返回成功给前端
        httpRequests.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //由于我们使用的是Restful风格的方式实现增删改查
    //即请求方式为POST：添加；GET：查询；DELETE：删除；PUT：添加。
    @PostMapping
    //因为保存之后不需要给前端传太多数据，所以依然是String，传来的是JSON数据所以用@RequestBody接收
    public R<String> addEmp(@RequestBody Employee employee,HttpServletRequest request){
        //获取当前操作的员工信息(已经通过自动导入完成操作)
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //将谁进行此次操作的信息传入数据库
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);
        //给每个员工默认设置123456作为初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        //设置操作时间(已经通过自动导入完成操作)
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //因为我们在数据库定义时对账号字段增加了唯一约束，所以当我们添加重复账号时会报错
        //我们可以通过先查询表中是否有该数据的方式判断，但这样要多写很多代码比较麻烦
        //也可以根据异常信息作为判断条件，当抛出数据库字段不唯一异常时，我们认为添加了重复字段
        //设置全局异常处理类GlobalExceptionHandler，当发生某个异常时调用其机制
        employeeService.save(employee);
        return R.success("添加成功");
    }

    //员工信息分页查询，采用MybatisPlus给我们提供的默认Page类作为返回值，因为前端中需要的是其中两个参数
    //name参数只有在查询时才会携带
    @GetMapping("/page")
    public R<Page<Employee>> employeePage(int page,int pageSize,String name){
        //Page的构造方法中第一个是当前页数，的第二个是一页要显示多少条数据
        Page<Employee> employeePage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //利用更新时间做一个排序，更新时间越早的越前面
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        Page<Employee> page1;
        if (name != null){
            //姓名的话可以用like，模糊查询，因为可能有的人叫张三，有的叫张三三，用户输入张三查询的时候应该两个都出来
            queryWrapper.like(Employee::getName,name);
        }
        //如果没传入name，就是说明不是名字查询，直接全表查询，排序方式为更新早的越先
        page1 = employeeService.page(employeePage,queryWrapper);
        return R.success(page1);
    }

    //根据id来修改员工信息的方法，使用的是put请求
    @PutMapping
    public R<String> empUpdate(@RequestBody Employee employee,HttpServletRequest request){
        //第一次尝试失败，原因是前端界面如果给后端传的是Long型数据，那么由于雪花算法长度过长
        //js会自动将这么长的Long型数据后三位四舍五入，导致精度丢失
        //正确做法应该是去修改前端代码，当我们查询好数据时，会先通过JSON类的方法将数据转成json格式给前端
        //我们可以从这点下手，在转成json数据时，将所有Long类型数据转成String类型数据给前端
        //在前端发回json类型数据给我们时，则会自动将String类型的数据转成我们需要的数据类型Long，且精度不会丢失
        //(已经通过自动导入完成操作)
        //Long updateUser = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(updateUser);
        //employee.setUpdateTime(LocalDateTime.now());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId,employee.getId());
        employeeService.update(employee,queryWrapper);
        return R.success("更改成功！");
    }

    //在点击编辑按钮后，会发送一个带参数请求，参数为id，是为了先查询出用户的数据，提前放入表格中方便修改
    @GetMapping("/{id}")
    public R<Employee> empMsgUpdate(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("未有该员工");
    }
}
