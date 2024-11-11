package com.tmesh.tmeshmall.member.controller;

import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;
import com.tmesh.tmeshmall.member.service.MemberReceiveAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 会员收货地址
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;


    /**
     * 根据会员id查询会员的所有地址
     * @param memberId
     * @return
     */
    @GetMapping(value = "/{memberId}/address")
    public List<MemberReceiveAddressEntity> getAddress(@PathVariable("memberId") Long memberId) {

        List<MemberReceiveAddressEntity> addressList = memberReceiveAddressService.getAddress(memberId);

        return addressList;
    }
    
    @GetMapping(value = "/{memberId}/addressForTable")
    public List<Map<String, Object>> getAddressForTable(@PathVariable("memberId") Long memberId) {

        List<Map<String, Object>> addressList = memberReceiveAddressService.getAddressForTableToMap(memberId);

        return addressList;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:memberreceiveaddress:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")

    //@RequiresPermissions("member:memberreceiveaddress:info")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    @RequestMapping("/transInfo/{id}")
    //@RequiresPermissions("member:memberreceiveaddress:info")
    public R transInfo(@PathVariable("id") Long id){
        MemberReceiveAddressEntity address = memberReceiveAddressService.getAddressInfoFromAllAddress(id);
        return R.ok().put("address", address);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:memberreceiveaddress:save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:memberreceiveaddress:update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
    
    @PostMapping("/modfiyDefaultStatus")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R modfiyDefaultStatus(@RequestBody Long id){
        boolean b = memberReceiveAddressService.modfiyDefaultStatus(id);
        if (b) {
            return R.ok(b);
        }
        return R.error(1, "修改失败");
    }

}
