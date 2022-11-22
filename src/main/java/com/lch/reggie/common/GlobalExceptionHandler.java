package com.lch.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author lch
 * @create 2022/10/30
 * Description:全局异常处理类
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //一旦有Controller抛出下列异常，就由该异常处理器处理
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException){
        String msg = "出错啦!";
        //每个异常类都属于一个大类，这个异常中也同样包含很多异常，我们可以把异常信息取出来，根据不同的异常信息确定该向客户端返回什么信息
        String message = sqlIntegrityConstraintViolationException.getMessage();
        //当信息中包含下列字符时，该异常是告诉我们数据库的唯一索引重复
        if (message.contains("Duplicate entry")){
            //在关于唯一索引重复的异常信息中，第三个词是告诉我们什么重复了，我们可以将其取出作为信息，告知用户这个账号重复
            String[] strings = message.split(" ");
            msg = strings[2] + "已存在";
        }
        return R.error(msg);
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException customException){
        String msg = "出错啦!";
        String message = customException.getMessage();
        if (message.contains("Dish Exist")){
            msg = "已关联菜品，无法删除";
        } else if(message.contains("Setmeal Exist")){
            msg = "已关联套餐，无法删除";
        } else if (message.contains("DISHES ASSOCIATE WITH SETMEALS STATUS")){
            msg = "与套餐关联菜品状态已停售，无法起售";
        } else if (message.contains("SETMEALS STATUS")){
            msg = "套餐状态非停售，无法删除";
        }
        return R.error(msg);
    }
}
