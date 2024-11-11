package com.tmesh.tmeshmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.member.MemberReceiveAddressVO;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据会员ID查询会员收货地址列表
     */
    List<MemberReceiveAddressEntity> getAddress(Long memberId);

    List<Map<String, Object>> getAddressForTableToMap(Long memberId);

    List<MemberReceiveAddressVO> getMemberReceiveAddressByMemberIdForTable(Long memberId);

    boolean modfiyDefaultStatus(Long id);

    MemberReceiveAddressEntity getAddressInfoFromAllAddress(Long id);
}

