package com.yy.blog.handler;

import com.alibaba.fastjson.JSON;
//import com.yy.blog.utils.UserThreadLocal;
import com.yy.blog.service.TokenService;
import com.yy.blog.vo.Result;
import com.yy.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在执行controller方法之前执行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        String token = request.getHeader("Authorization");

        if(StringUtils.isBlank(token)){
            Result result = Result.fail(-800, "please login");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        Object userId = tokenService.checkToken(token);
        if(userId==null){
            Result result = Result.fail(-801, "login expired");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        // 成功 放行
        // controller中直接拿用户信息
//        UserThreadLocal.put(sysUser);
        return true;
    }

//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // 如果不删除 ThreadLocal 会有内存泄露风险
//        UserThreadLocal.remove();
//    }
}
