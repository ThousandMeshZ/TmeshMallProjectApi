package com.tmesh.tmeshmall.member.controller;

import com.tmesh.common.exception.BizCodeEnum;
import com.tmesh.common.exception.PhoneException;
import com.tmesh.common.exception.UsernameException;
import com.tmesh.common.to.member.MemberUserLoginTO;
import com.tmesh.common.to.member.MemberUserRegisterTO;
import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.member.MemberUserLoginVO;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.common.vo.member.SocialUser;
import com.tmesh.tmeshmall.member.entity.AddressEntity;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.tmeshmall.member.feign.CouponFeignService;
import com.tmesh.tmeshmall.member.service.AddressService;
import com.tmesh.tmeshmall.member.service.MemberService;
import com.tmesh.tmeshmall.member.service.impl.AddressServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 会员
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
@RestController
@RequestMapping("member/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = addressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        AddressEntity member = addressService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody AddressEntity address) {
        addressService.save(address);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody AddressEntity address) {
        boolean b = addressService.updateById(address);

        return R.ok(null, b);
    }

    /**
     * `
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        addressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    
    @GetMapping("/allAddress")
    public R allAddress() {
        List<Map<String, Object>> list =  addressService.getAllAddress();

        return R.ok(list);
    }
    
    @GetMapping("/addressTree")
    public R addressTree(@RequestParam("parentId") Long parentId) {
        List<Map<String, Object>> list =  addressService.getAddressTree(parentId);
        return R.ok(list);
    }
}
