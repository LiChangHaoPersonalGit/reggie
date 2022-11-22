package com.lch.reggie.interrupt;

import com.alibaba.fastjson.JSON;
import com.lch.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lch
 * @create 2022/10/29
 * Description:定义拦截器
 */

@Slf4j
public class LoginHandlerInterrupt implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取一下本次请求的URI，当请求为登录界面时不需要拦截
        String requestURI = request.getRequestURI();
        //定义不需要拦截的请求
        String[] urls = new String[]{
            "/employee/login", "/employee/logout","/user/login","/user/sendMsg"
        };
        //判断是否是不需要拦截的请求
        if (checkUri(urls,requestURI)){
            return true;
        }
        //虽然是需要拦截的请求，但是要是登录了就不拦截
        if (request.getSession().getAttribute("employee") != null){
            return true;
        }
        if (request.getSession().getAttribute("user") != null){
            return true;
        }
        //因为我们前端存在一个响应拦截器，详情请看前端js中的request.js，只要向前端发送的数据是个error数据且携带msg为NOTLOGIN，则会跳转回登录页
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("拦截成功");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean checkUri(String[] urls,String uri){
        for (String url : urls){
            if (uri.equals(url)){
                return true;
            }
        }
        return false;
    }
}
