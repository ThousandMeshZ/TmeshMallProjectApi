package com.tmesh.tmeshmall.seckill.interceptor;

import com.tmesh.common.vo.auth.MemberResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;

import static com.tmesh.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * @Description: 登录拦截器
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 * @createTime: 2024-01-02 18:37
 **/

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVO> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/kill", uri);

        if (match) {
            HttpSession session = request.getSession();
            //获取登录的用户信息
            MemberResponseVO attribute = (MemberResponseVO) session.getAttribute(LOGIN_USER);
            if (attribute != null) {
                //把登录后用户的信息放在ThreadLocal里面进行保存
                loginUser.set(attribute);
                return true;
            } else {
                //未登录，返回登录页面
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.tmesh.cn/login.html'</script>");
                // session.setAttribute("msg", "请先进行登录");
                // response.sendRedirect("http://auth.tmesh.cn/login.html");
                return false;
            }
        }
        return true;
    }

    
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
