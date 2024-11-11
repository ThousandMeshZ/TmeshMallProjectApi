package com.tmesh.tmeshmall.member.interceptor;

import com.tmesh.common.constant.auth.AuthConstant;
import com.tmesh.common.vo.auth.MemberResponseVO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static com.tmesh.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * @Description: 登录拦截器
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVO> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行无需登录的请求
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();// 匹配器
        boolean match = new AntPathMatcher().match("/**.html", uri);
        boolean match1 = new AntPathMatcher().match("/**.do", uri);
        System.out.println("登录拦截请求：" + uri);
        if (!match && !match1) {
            return true;
        }

        // 获取登录用户信息
        MemberResponseVO attribute = (MemberResponseVO) request.getSession().getAttribute(AuthConstant.LOGIN_USER);
        if (attribute != null) {
            // 已登录，放行
            // 封装用户信息到threadLocal
            loginUser.set(attribute);
            return true;
        } else {
            // 未登录，跳转登录页面
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.tmesh.cn/login.html'</script>");
            // session.setAttribute("msg", "请先进行登录");
            // response.sendRedirect("http://auth.temsh.cn/login.html");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
