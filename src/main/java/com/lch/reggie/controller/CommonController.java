package com.lch.reggie.controller;

import com.lch.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author lch
 * @create 2022/11/7
 * Description:文件上传下载Controller
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //我们在application.yml中定义了自定义参数，可以通过这种方式将值导入
    @Value("${reggie.path}")
    private String filePath;

    //文件上传
    //前端upload.html中给出的input的name为file，所以下面也得用file
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //刚获得的file会被服务器暂存，但是如果不用输入流存起来的话，本次请求结束就会删除
        //原始文件名，不建议用作文件名赋值，因为上传的文件可能存在文件名重复的情况
        String originalFilename = file.getOriginalFilename();
        //推荐使用uuid给文件名赋值，防止文件名重复
        String uuid = UUID.randomUUID().toString();
        //将文件名后缀获取
        String uuidFilename = uuid + originalFilename.substring(originalFilename.lastIndexOf("."));
        //创建目录对象
        File dir = new File(filePath);
        //如果目录不存在就创建，不然会报错
        if (!dir.exists()){
            dir.mkdirs();
        }
        //文件转存
        try {
            file.transferTo(new File(filePath + uuidFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将文件名返回给页面，因为上传完图片后，用户会点击添加按钮，此时会将文件名作为字段值存入数据库中
        return R.success(uuidFilename);
    }

    //文件下载
    @GetMapping("/download")
    public void download(@PathParam("name") String name, HttpServletResponse response){
        try {
            //创建输入流先从磁盘读取该图片
            FileInputStream fileInputStream = new FileInputStream(new File(filePath + name));
            //创建输出流将读出的文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            //这句话的意思是告诉response我们要响应的是图片文件，标准写法
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
