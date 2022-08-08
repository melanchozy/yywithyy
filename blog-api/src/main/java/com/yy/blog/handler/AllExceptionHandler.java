package com.yy.blog.handler;

import com.yy.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 对加了@Controller的方法进行拦截处理
@ControllerAdvice
public class AllExceptionHandler {
    // 进行异常处理
    @ResponseBody //返回json数据
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex){
        ex.printStackTrace();
        return Result.fail(-999,"System error");
    }
}
