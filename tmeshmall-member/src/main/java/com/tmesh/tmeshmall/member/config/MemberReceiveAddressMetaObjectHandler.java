package com.tmesh.tmeshmall.member.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tmesh.common.utils.EmptyUtils;
import com.tmesh.tmeshmall.member.entity.AddressEntity;
import com.tmesh.tmeshmall.member.entity.MemberReceiveAddressEntity;
import com.tmesh.tmeshmall.member.service.AddressService;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class MemberReceiveAddressMetaObjectHandler implements MetaObjectHandler {

    @Lazy
    @Autowired
    private AddressService addressService;
    @Override
    public void insertFill(MetaObject metaObject) {
        this.fillStrategy(metaObject, "defaultStatus", 0);
        MemberReceiveAddressEntity m = (MemberReceiveAddressEntity) metaObject.getOriginalObject();
        String allAddress = (EmptyUtils.isNotEmpty(m.getProvince()) && EmptyUtils.isNotEmpty(m.getCity()) && EmptyUtils.isNotEmpty(m.getRegion())) ?
                m.getProvince() + "\\" + m.getCity() + "\\" + m.getRegion():
                (EmptyUtils.isNotEmpty(m.getProvince()) && EmptyUtils.isNotEmpty(m.getCity())) ?
                        m.getProvince() + "\\" + m.getCity():
                        (EmptyUtils.isNotEmpty(m.getProvince())) ?
                                m.getProvince(): "";
        if (EmptyUtils.isNotEmpty(allAddress)) {
            AddressEntity address = addressService.getByAllAddress(allAddress);
            if (EmptyUtils.isNotEmpty(address)) {
                this.fillStrategy(metaObject, "areacode", String.valueOf(address.getId()));
            }
        }
                
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        MemberReceiveAddressEntity m = (MemberReceiveAddressEntity) metaObject.getOriginalObject();
        String allAddress = (EmptyUtils.isNotEmpty(m.getProvince()) && EmptyUtils.isNotEmpty(m.getCity()) && EmptyUtils.isNotEmpty(m.getRegion())) ?
                m.getProvince() + "\\" + m.getCity() + "\\" + m.getRegion():
                (EmptyUtils.isNotEmpty(m.getProvince()) && EmptyUtils.isNotEmpty(m.getCity())) ?
                        m.getProvince() + "\\" + m.getCity():
                        (EmptyUtils.isNotEmpty(m.getProvince())) ?
                                m.getProvince(): "";
        if (EmptyUtils.isNotEmpty(allAddress)) {
            AddressEntity address = addressService.getByAllAddress(allAddress);
            if (EmptyUtils.isNotEmpty(address)) {
                this.fillStrategy(metaObject, "areacode", String.valueOf(address.getId()));
            }
        }
    }
}