package com.tmesh.tmeshmall.member.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.R;
import com.tmesh.common.vo.auth.MemberResponseVO;
import com.tmesh.common.vo.member.MemberReceiveAddressVO;
import com.tmesh.common.vo.product.Catalog2VO;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;
import com.tmesh.tmeshmall.member.feign.OrderFeignService;
import com.tmesh.tmeshmall.member.interceptor.LoginUserInterceptor;
import com.tmesh.tmeshmall.member.service.AddressService;
import com.tmesh.tmeshmall.member.service.MemberReceiveAddressService;
import com.tmesh.tmeshmall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Controller
public class MemberWebController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;
    
    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderFeignService orderFeignService;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum,
                                  Model model, HttpServletRequest request) {

        // 获取支付宝回参，根据sign延签，延签成功修改订单状态【不建议在同步回调修改订单状态，建议在异步回调修改订单状态】
        //request,验证签名


        //查出当前登录用户的所有订单列表数据
        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum.toString());

        // 分页查询当前用户的订单列表、订单项
        R orderInfo = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(orderInfo));
        model.addAttribute("orders",orderInfo);

        return "orderList";
    }

    @GetMapping(value = "/order.json")
    @ResponseBody
    public JSONObject getCatalogJson(@RequestParam(value = "page",required = false,defaultValue = "0") Integer page,
                                                        @RequestParam(value = "limit",required = false,defaultValue = "10") Integer limit) {

        Map<String,Object> pageMap = new HashMap<>();
        pageMap.put("page", String.valueOf(page));
        pageMap.put("limit", String.valueOf(limit));
        R r = orderFeignService.listWithItemForTable(pageMap);
        JSONObject orderInfo = new JSONObject();
        Map<String, Object> pageInfo = (Map<String, Object>) r.get("page");
        orderInfo.put("code", r.get("code"));
        orderInfo.put("data", pageInfo.get("list"));
        orderInfo.put("count", pageInfo.get("totalCount"));
        return orderInfo;

    }

    @GetMapping(value = "/memberInfo.html")
    public String memberInforPage(Model model, HttpServletRequest request) {
        
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();
        MemberEntity memberEntity = memberService.getById(member.getId());
        
        System.out.println("memberEntity: " + JSON.toJSONString(memberEntity));
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(memberEntity));

        model.addAttribute("member", jsonObject);

        return "memberInfo";
    }

    @GetMapping(value = "/addressInfo.html")
    public String addressInforPage(Model model, HttpServletRequest request) {
        
        MemberResponseVO member = LoginUserInterceptor.loginUser.get();
        List<Map<String, Object>> memberAddress = memberReceiveAddressService.getAddressForTableToMap(member.getId());
//        List<MemberReceiveAddressVO> memberAddress = memberReceiveAddressService.getMemberReceiveAddressByMemberIdForTable(member.getId());
//        String memberAddressJson = JSON.toJSONString(memberAddress);
        model.addAttribute("memberAddress", memberAddress);

        List<Map<String, Object>> address = addressService.getAddressTree(0L);
        model.addAttribute("addressList", address);

        return "addressInfo";
    }

    @PostMapping("/updateMemberInfo.do")
    @ResponseBody
    public R updateMemberInfo(@RequestBody MemberEntity member) {
        MemberResponseVO loginUser = LoginUserInterceptor.loginUser.get();
        boolean b = memberService.updateMemberInfo(loginUser.getId(), member);
        return R.ok(null, b);
    }

    @PostMapping("/updateMemberAddressInfo.do")
    @ResponseBody
    public R updateMemberAddressInfo(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
        boolean b = memberReceiveAddressService.updateById(memberReceiveAddress);;
        if (!b) {
            return R.error(1, "保存失败");
        }
        Map<String, Object> result = new HashMap<>();
        return R.ok(null, true);
    }

    @PostMapping("/saveMemberAddressInfo.do")
    @ResponseBody
    public R saveMemberAddressInfo(@RequestBody MemberReceiveAddressEntity memberReceiveAddress) {
        MemberResponseVO loginUser = LoginUserInterceptor.loginUser.get();
        memberReceiveAddress.setMemberId(loginUser.getId());
        boolean b = memberReceiveAddressService.save(memberReceiveAddress);;
        if (!b) {
            return R.error(1, "保存失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", memberReceiveAddress.getId());
        result.put("memberId", memberReceiveAddress.getMemberId());
        return R.ok(null, result);
    }

}
