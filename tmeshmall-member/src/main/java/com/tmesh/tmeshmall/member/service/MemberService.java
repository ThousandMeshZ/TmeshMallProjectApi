package com.tmesh.tmeshmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.to.member.MemberUserLoginTO;
import com.tmesh.common.to.member.MemberUserRegisterTO;
import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.common.exception.PhoneException;
import com.tmesh.common.exception.UsernameException;
import com.tmesh.common.vo.member.MemberUserLoginVO;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.common.vo.member.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 用户注册
     * @param vo
     */
    void register(MemberUserRegisterVO vo);

    /**
     * 判断邮箱是否重复
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 判断用户名是否重复
     * @param userName
     * @return
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    /**
     * 用户登录
     * @param vo
     * @return
     */
    MemberEntity login(MemberUserLoginVO vo);

    /**
     * 登录
     */
    MemberEntity login(MemberUserLoginTO user);

    /**
     * 社交用户的登录
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser) throws Exception;

    /**
     * 微信登录
     * @param accessTokenInfo
     * @return
     */
    MemberEntity login(String accessTokenInfo);

    /**
     * 微博社交登录（登录和注册功能合并）
     */
    MemberEntity login(WBSocialUserTO user) throws Exception;

    /**
     * 注册
     */
    void regist(MemberUserRegisterTO user) throws InterruptedException;

    /**
     *
     */
    boolean existUserByUsername(String username);

    /**
     *
     */
    boolean existUserByMobile(String mobile);
    
    boolean updateMemberInfo(Long id, MemberEntity member);
}

