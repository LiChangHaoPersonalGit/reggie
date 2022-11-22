package com.lch.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author lch
 * @create 2022/11/3
 * Description:自定义元数据对象处理器
 */

@Component
@Slf4j
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    //创建字段时执行
    @Override
    public void insertFill(MetaObject metaObject) {
        //任何插入操作，都会执行一次该方法
        //自动注入其实就是调用setValue方法为某个属性设置值即可
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long id;
        if (request.getSession().getAttribute("employee") != null){
            id = (Long) request.getSession().getAttribute("employee");
        } else {
            id = (Long) request.getSession().getAttribute("user");
        }
        //已被HttpServletRequest注入替代
        //Long id = BaseContext.GetCurrentId();
        metaObject.setValue("createUser", id);
        metaObject.setValue("updateUser", id);
    }

    //更新字段时执行
    @Override
    public void updateFill(MetaObject metaObject) {
        //任何更新操作，都会执行一次该方法，而metaObject中存的就是一个数据库数据对象，其中每个属性对应一个字段名(小驼峰)
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Long id;
        if (request.getSession().getAttribute("employee") != null){
            id = (Long) request.getSession().getAttribute("employee");
        } else {
            id = (Long) request.getSession().getAttribute("user");
        }
        //已被HttpServletRequest注入替代
        //Long id = BaseContext.GetCurrentId();
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser", id);
    }
}
