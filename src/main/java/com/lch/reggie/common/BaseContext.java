package com.lch.reggie.common;

/**
 * @author lch
 * @create 2022/11/4
 * Description:ThreadLocal工具类
 * ThreadLocal：每个请求都会由服务端的一个线程管理
 * 这个线程会先经过Filter(我们使用拦截器代替，所以无法使用)，Controller以及MetaObjectHandler中的update方法
 * 而ThreadLocal是lang包用来处理线程的一个方法，其本身并不是一个线程，但是其set和get方法，却可以让其在一个线程中绑定数据
 * 这份数据仅该线程独有，这正符合我们需要使用服务端发出的线程携带id的想法
 */

public class BaseContext {
    //因为我们使用ThreadLocal存储的是id变量，是Long型
    static private ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    static public void SetCurrentId(Long id){
        threadLocal.set(id);
    }

    static public Long GetCurrentId(){
        return threadLocal.get();
    }
}
