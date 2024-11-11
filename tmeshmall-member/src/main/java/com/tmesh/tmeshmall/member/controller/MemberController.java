package com.tmesh.tmeshmall.member.controller;

import com.tmesh.common.exception.BizCodeEnum;
import com.tmesh.common.to.member.MemberUserLoginTO;
import com.tmesh.common.to.member.MemberUserRegisterTO;
import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.MemberResponseVO;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.common.exception.PhoneException;
import com.tmesh.common.exception.UsernameException;
import com.tmesh.tmeshmall.member.feign.CouponFeignService;
import com.tmesh.tmeshmall.member.interceptor.LoginUserInterceptor;
import com.tmesh.tmeshmall.member.service.MemberService;
import com.tmesh.common.vo.member.MemberUserLoginVO;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.common.vo.member.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private CouponFeignService couponFeignService;

    /**
     * openFeign测试接口
     */
    @RequestMapping("/coupons")
    public R test() {
        MemberEntity entity = new MemberEntity();
        entity.setNickname("张三");

        R membercoupons = couponFeignService.membercoupons();
        Object coupons = membercoupons.get("coupons");
        return R.ok().put("member", entity).put("coupons", coupons);
    }

    @PostMapping(value = "/register")
    public R register(@RequestBody MemberUserRegisterVO vo) {

        try {
            memberService.register(vo);
        } catch (PhoneException e) {
            e.printStackTrace();
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMessage());
        } catch (UsernameException e) {
            e.printStackTrace();
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

        return R.ok();
    }


    @PostMapping(value = "/login")
    public R login(@RequestBody MemberUserLoginVO vo) {
        try {
            MemberEntity memberEntity = memberService.login(vo);

            if (memberEntity != null) {
                return R.ok().setData(memberEntity);
            } else {
                return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

    }


    @PostMapping(value = "/oauth2/login")
    public R oauthLogin(@RequestBody SocialUser socialUser) throws Exception {
        try {
            MemberEntity memberEntity = memberService.login(socialUser);

            if (memberEntity != null) {
                return R.ok().setData(memberEntity);
            } else {
                return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

    }

    @PostMapping(value = "/weixin/login")
    public R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo) {
        try {
            MemberEntity memberEntity = memberService.login(accessTokenInfo);
            if (memberEntity != null) {
                return R.ok().setData(memberEntity);
            } else {
                return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        boolean b = memberService.updateById(member);

        return R.ok(null, b);
    }

    /**
     * `
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 注册
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberUserRegisterTO user) {
        try {
            memberService.regist(user);
            return R.ok();
        } catch (PhoneException ex) {
            ex.printStackTrace();
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION);
        } catch (UsernameException ex) {
            ex.printStackTrace();
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

    }

    /**
     *
     */
    @PostMapping("/existUserByUsername")
    public R existUserByUsername(@RequestBody String username) {
        boolean exist = false;
        try {
            exist = memberService.existUserByUsername(username);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }
        return R.ok().setData(exist);
    }

    /**
     *
     */
    @PostMapping("/existUserByMobile")
    public R existUserByMobile(@RequestBody String mobile) {
        boolean exist = false;
        try {
            exist = memberService.existUserByMobile(mobile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }
        return R.ok().setData(exist);
    }

    /**
     * 登录
     */
    @PostMapping("/loginTo")
    public R login(@RequestBody MemberUserLoginTO user) {
        try {
            MemberEntity entity = memberService.login(user);
            if (entity == null) {
                return R.error(BizCodeEnum.LOGINACCT_PASSWORD_EXCEPTION);
            }
            return R.ok().setData(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }

    }

    /**
     * 微博社交登录
     */
    @PostMapping("/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserTO user) {
        try {
            MemberEntity entity = memberService.login(user);
            return R.ok().setData(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
            return R.error(ex.getMessage());
        }
    }
}
