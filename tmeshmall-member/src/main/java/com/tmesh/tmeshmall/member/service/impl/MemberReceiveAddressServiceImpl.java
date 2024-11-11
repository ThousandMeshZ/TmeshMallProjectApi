package com.tmesh.tmeshmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.common.vo.auth.MemberResponseVO;
import com.tmesh.common.vo.member.MemberReceiveAddressVO;
import com.tmesh.tmeshmall.member.dao.MemberReceiveAddressDao;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;
import com.tmesh.tmeshmall.member.interceptor.LoginUserInterceptor;
import com.tmesh.tmeshmall.member.service.MemberReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getAddress(Long memberId) {

        List<MemberReceiveAddressEntity> addressList = this.baseMapper.selectList
                (new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId));

        return addressList;
    }

    @Override
    public List<Map<String, Object>> getAddressForTableToMap(Long memberId) {
        QueryWrapper<MemberReceiveAddressEntity> queryWrapper = new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", memberId);
        queryWrapper.select("id", "name", "phone", "province",
                "city", "region", "detail_address as detail", "default_status", "default_status=1 as LAY_CHECKED",
                "IF ((region is not null and region != ''), CONCAT_WS('\\\\', province, city, region), IF ((city is not null and city != ''), CONCAT_WS('\\\\', province, city), IF ((province is not null and province != ''), province, ''))) AS area");
        List<Map<String, Object>> addressList = this.baseMapper.selectMaps(queryWrapper);
        return addressList;
    }


    @Override
    public List<MemberReceiveAddressVO> getMemberReceiveAddressByMemberIdForTable(Long memberId) {
        return this.baseMapper.getMemberReceiveAddressByMemberIdForTable(memberId);
    }

    @Override
    public boolean modfiyDefaultStatus(Long id) {
        QueryWrapper<MemberReceiveAddressEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("default_status", 1).
                or().
                eq("id", id);
        List<MemberReceiveAddressEntity> receiveAddressList = this.baseMapper.selectList(queryWrapper);
        receiveAddressList.forEach(item -> {
            if (item.getDefaultStatus() == null || item.getDefaultStatus() == 0) {
                item.setDefaultStatus(1);
            } else {
                item.setDefaultStatus(0);
            }
        });
        boolean batch = this.updateBatchById(receiveAddressList);
        return batch;
    }

    @Override
    public MemberReceiveAddressEntity getAddressInfoFromAllAddress(Long id) {
        MemberReceiveAddressEntity address = this.getById(id);
        Map<String, String> info = this.baseMapper.getAddressInfoFromAllAddress(id);
        address.setProvince(info.get("province"));
        address.setCity(info.get("city"));
        address.setRegion(info.get("region"));
        return address;
    }

}