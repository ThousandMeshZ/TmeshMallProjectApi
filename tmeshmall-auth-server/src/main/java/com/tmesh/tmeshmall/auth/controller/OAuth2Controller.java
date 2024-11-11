package com.tmesh.tmeshmall.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tmesh.common.constant.auth.AuthConstant;
import com.tmesh.common.utils.HttpUtils;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.MemberResponseVO;
import com.tmesh.common.vo.auth.WBSocialUserVO;
import com.tmesh.tmeshmall.auth.agent.MemberAgentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交登录
 *
 * @Author: TMesh
 * @Date: 2024/01/26 22:26
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    MemberAgentService memberAgentService;

    /**
     * 授权回调页
     *
     * @param code 根据code换取Access Token，且code只能兑换一次Access Token
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        // 1.根据code换取Access Token
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "2882737846");
        map.put("client_secret", "304f7cc2be9499ebdc48ed2d75e55373");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.tmesh.cn/oauth2.0/weibo/success");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", headers, querys, map);

        // 2.处理请求返回
        if (response.getStatusLine().getStatusCode() == 200) {
            // 换取Access_Token成功
            String jsonString = EntityUtils.toString(response.getEntity());
            WBSocialUserVO user = JSONObject.parseObject(jsonString, WBSocialUserVO.class);

            // 首次登录自动注册（为当前社交登录用户生成一个会员账号信息，以后这个社交账户就会对应指定的会员）
            // 非首次登录则直接登录成功
            R r = memberAgentService.oauthLogin(user);
            if (r.getCode() == 0) {
                // 登录成功
                MemberResponseVO loginUser = r.getData(new TypeReference<>() {
                });
                log.info("登录成功：用户：{}", loginUser.toString());
                //1、第一次使用 session;命令浏览器保存卡号。JSESSIONID 这个 cookie;
                //以后浏览器访问哪个网站就会带上这个网站的 cookie;
                //子域之间;tmesh.cn auth.tmesh.cn order.tmesh.cn
                //发卡的时候(指定域名为父域名)，即使是子域系统发的卡，也能让父域直接使用。
                //TODO 1、默认发的令牌。session=dsajkdjl。作用域:当前域;(解决子域 session 共享问题)
                //TODO 2、使用JsoN的序列化方式来序列化对象数据到 redis 中

                // 3.信息存储到 session 中，并且放大作用域（指定 domain=父级域名）
                session.setAttribute(AuthConstant.LOGIN_USER, loginUser);
                // 首次使用 session 时，spring 会自动颁发 cookie 设置 domain，所以这里手动设置 cookie 很麻烦，
                // 采用 springsession 的方式颁发父级域名的 domain 权限，在 SessionConfig 中配置
//                Cookie cookie = new Cookie("JSESSIONID", loginUser.getId().toString());
//                cookie.setDomain("tmesh.cn");
//                servletResponse.addCookie(cookie);
                // 跳回首页
                return "redirect:http://tmesh.cn";
            } else {
                // 登录失败，返回登录页
                return "redirect:http://auth.tmesh.cn/login.html";
            }
        } else {
            // 换取Access_Token失败，返回登录页
            return "redirect:http://auth.tmesh.cn/login.html";
        }
    }


}