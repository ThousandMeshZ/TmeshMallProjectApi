package com.tmesh.tmeshmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.tmesh.common.constant.member.MemberConstant;
import com.tmesh.common.exception.PhoneException;
import com.tmesh.common.exception.UsernameException;
import com.tmesh.common.to.member.MemberUserLoginTO;
import com.tmesh.common.to.member.MemberUserRegisterTO;
import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.*;
import com.tmesh.common.vo.member.MemberUserLoginVO;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.common.vo.member.SocialUser;
import com.tmesh.tmeshmall.member.dao.AddressDao;
import com.tmesh.tmeshmall.member.dao.MemberDao;
import com.tmesh.tmeshmall.member.dao.MemberLevelDao;
import com.tmesh.tmeshmall.member.entity.AddressEntity;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.tmeshmall.member.entity.MemberLevelEntity;
import com.tmesh.tmeshmall.member.service.AddressService;
import com.tmesh.tmeshmall.member.service.MemberService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("addressService")
public class AddressServiceImpl extends ServiceImpl<AddressDao, AddressEntity> implements AddressService {

    @Resource
    private AddressDao addressDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AddressEntity> page = this.page(
                new Query<AddressEntity>().getPage(params),
                new QueryWrapper<AddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public AddressEntity getByAllAddress(String allAddress) {
        QueryWrapper<AddressEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("all_address", allAddress);
        List<AddressEntity> list = this.baseMapper.selectList(queryWrapper);
        if (EmptyUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error(allAddress + " 有多个对应地址");
            }
            return list.get(0);
        } else {
            log.error(allAddress + " 没有对应地址");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllAddress() {
        return addressDao.getAllAddress();
    }

    @Override
    public List<Map<String, Object>> getAddressTree(Long parentId) {
        List<Map<String, Object>> addressTree = addressDao.getAddressByParent(parentId);
        return addressTree;
    }

    @Override
    public List<Map<String, Object>> getAllAddressTree() {
        List<Map<String, Object>> result = (List<Map<String, Object>>) redisTemplate.opsForValue().get("addressTree");
        if (EmptyUtils.isNotEmpty(result)) {
            return  result;
        }
        List<Map<String, Object>> addressTree = addressDao.getAddressByParent(0L);
        addressTree.forEach(address -> getAddressTreeChildren(address));
        redisTemplate.opsForValue().set("addressTree", addressTree);
        return addressTree;
    }
    
    private void getAddressTreeChildren(Map<String, Object> address) {
        List<Map<String, Object>> addressTree = addressDao.getAddressByParent((Long) address.get("id"));
        address.put("children", addressTree);
        addressTree.forEach(address1 -> getAddressTreeChildren(address1));
    }
    

}