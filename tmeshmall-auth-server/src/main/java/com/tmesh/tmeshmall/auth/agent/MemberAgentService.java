package com.tmesh.tmeshmall.auth.agent;

import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.WBSocialUserVO;
import com.tmesh.tmeshmall.auth.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberAgentService {

    @Autowired
    MemberFeignService memberFeignService;

    public R oauthLogin(WBSocialUserVO user) {
        WBSocialUserTO param = new WBSocialUserTO();
        param.setAccessToken(user.getAccess_token());
        param.setExpiresIn(user.getExpires_in());
        param.setRemindIn(user.getRemind_in());
        param.setIsRealName(user.getIsRealName());
        param.setUid(user.getUid());
        return memberFeignService.oauthLogin(param);
    }
}
