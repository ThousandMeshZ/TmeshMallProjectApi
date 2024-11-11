package com.tmesh.tmeshmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.tmeshmall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author TMesh
 * @email 1009191578@qq.com
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

