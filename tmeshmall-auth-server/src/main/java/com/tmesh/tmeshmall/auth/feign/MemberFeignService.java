package com.tmesh.tmeshmall.auth.feign;

import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.UserLoginVO;
import com.tmesh.common.vo.auth.UserRegisterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 会员服务
 * @Author: TMesh
 * @Date: 2024/01/28 19:52
 */
@FeignClient("tmeshmall-member")
public interface MemberFeignService {

    /**
     *
     */
    @PostMapping("/member/member/existUserByUsername")
    R existUserByUsername(@RequestBody String username);

    /**
     *
     */
    @PostMapping("/member/member/existUserByMobile")
    R existUserByMobile(@RequestBody String mobile);

    /**
     * 注册
     */
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVO user);

    /**
     * 登录
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);

    /**
     * 微博社交登录
     */
    @PostMapping("/member/member/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserTO user);

    @PostMapping(value = "/member/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}