package com.tmesh.tmeshmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.tmeshmall.member.entity.AddressEntity;
import com.tmesh.tmeshmall.member.entity.MemberEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface AddressService extends IService<AddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AddressEntity getByAllAddress(String allAddress);

    List<Map<String, Object>> getAllAddress();

    List<Map<String, Object>> getAddressTree(Long parentId);

    List<Map<String, Object>> getAllAddressTree();
}

