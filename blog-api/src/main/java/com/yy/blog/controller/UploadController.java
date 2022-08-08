package com.yy.blog.controller;

import com.yy.blog.utils.QiniuUtils;
import com.yy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {
//    @Autowired
//    private QiniuUtils qiniuUtils;

//    @PostMapping
//    public Result upload(@RequestParam("image") MultipartFile file){
//        // 唯一的文件名
////        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
//        String fileName= file.getOriginalFilename();
//        boolean upload=qiniuUtils.upload(file,fileName);
//        if(upload){
//            return Result.success(QiniuUtils.url+fileName);
//        }
//        return Result.fail(-9988,"上传失败");
//    }
    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file) {
        try {
            //获取文件名
            String fileName = file.getOriginalFilename();
            //获取文件后缀名。也可以在这里添加判断语句，规定特定格式的图片才能上传，否则拒绝保存。
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //为了避免发生图片替换，这里使用了文件名重新生成
            fileName = UUID.randomUUID() + suffixName;

//            String path = ResourceUtils.getURL("classpath:").getPath() + "static/img/";
            //获取jar包所在目录
            ApplicationHome h = new ApplicationHome(getClass());
            File jarF = h.getSource();
            //在jar包所在目录下生成一个upload文件夹用来存储上传的图片
            String path = jarF.getParentFile().toString()+"/static/img/";
            System.out.println(path);
            File dir=new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            file.transferTo(new File(path + fileName));

            return Result.success("http://1.15.245.33:8888/static/img/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(20001,"上传失败");
        }

    }

}
