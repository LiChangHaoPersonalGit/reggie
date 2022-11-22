package com.lch.reggie.config;

import com.lch.reggie.common.JacksonObjectMapper;
import com.lch.reggie.interrupt.LoginHandlerInterrupt;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author lch
 * @create 2022/10/29
 * Description:WebMvc的配置类
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterrupt())
                .addPathPatterns("/**")
                .excludePathPatterns("/backend/**","/front/**");
    }

    //自定义消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //这是Mvc底层用于将响应内容转为json格式的消息转换器
        //是否会转换成json取决于我们浏览器的accept权重，即浏览器能显示的信息的权重以及我们服务器能发出去的消息的类型
        //这里我们属于是新创建了一个转换json格式的消息转换器，但和原本就有的不冲突
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //index即优先级，0是最高优先级
        //只有将优先级设置高了，才会优先使用我们自定义的消息转换器，不然就会使用系统默认的消息转换器
        converters.add(0,messageConverter);
    }
}
