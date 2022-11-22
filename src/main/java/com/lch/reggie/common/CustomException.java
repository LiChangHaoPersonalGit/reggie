package com.lch.reggie.common;

/**
 * @author lch
 * @create 2022/11/4
 * Description:自定义业务异常
 */

public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
