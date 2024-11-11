package com.tmesh.tmeshmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.tmesh.common.constant.member.MemberConstant;
import com.tmesh.common.to.member.MemberUserLoginTO;
import com.tmesh.common.to.member.MemberUserRegisterTO;
import com.tmesh.common.to.member.WBSocialUserTO;
import com.tmesh.common.utils.HttpUtils;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;
import com.tmesh.tmeshmall.member.dao.MemberDao;
import com.tmesh.tmeshmall.member.dao.MemberLevelDao;
import com.tmesh.tmeshmall.member.entity.MemberEntity;
import com.tmesh.tmeshmall.member.entity.MemberLevelEntity;
import com.tmesh.common.exception.PhoneException;
import com.tmesh.common.exception.UsernameException;
import com.tmesh.tmeshmall.member.service.MemberService;
import com.tmesh.common.utils.HttpClientUtils;
import com.tmesh.common.vo.member.MemberUserLoginVO;
import com.tmesh.common.vo.member.MemberUserRegisterVO;
import com.tmesh.common.vo.member.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    MemberLevelServiceImpl memberLevelService;

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberUserRegisterVO vo) {

        MemberEntity memberEntity = new MemberEntity();

        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //设置其它的默认信息
        //检查用户名和手机号是否唯一。感知异常，异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        memberEntity.setNickname(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        //密码进行MD5加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setGender(0);
        memberEntity.setCreateTime(new Date());

        //保存数据
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {

        Long phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));

        if (phoneCount > 0) {
            throw new PhoneException();
        }

    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameException {

        Long usernameCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));

        if (usernameCount > 0) {
            throw new UsernameException();
        }
    }

    @Override
    public MemberEntity login(MemberUserLoginVO vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1、去数据库查询 SELECT * FROM ums_member WHERE username = ? OR mobile = ?
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));

        if (memberEntity == null) {
            //登录失败
            return null;
        } else {
            //获取到数据库里的password
            String password1 = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //进行密码匹配
            boolean matches = passwordEncoder.matches(password, password1);
            if (matches) {
                //登录成功
                return memberEntity;
            }
        }

        return null;
    }

    /**
     * 登录
     */
    @Override
    public MemberEntity login(MemberUserLoginTO user) {
        String loginacct = user.getLoginacct();
        String password = user.getPassword();// 明文

        // 1.查询MD5密文
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct)
                .or()
                .eq("mobile", loginacct));
        if (entity != null) {
            // 2.获取password密文进行校验
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, entity.getPassword())) {
                // 登录成功
                return entity;
            }
        }
        // 3.登录失败
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {

        //具有登录和注册逻辑
        String uid = socialUser.getUid();

        //1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (memberEntity != null) {
            //这个用户已经注册过
            //更新用户的访问令牌的时间和access_token
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        } else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", new HashMap<String, String>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender)?1:0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                this.baseMapper.insert(register);

            }
            return register;
        }

    }

    @Override
    public MemberEntity login(String accessTokenInfo) {

        //从accessTokenInfo中获取出来两个值 access_token 和 oppenid
        //把accessTokenInfo字符串转换成map集合，根据map里面中的key取出相对应的value
        Gson gson = new Gson();
        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) accessMap.get("access_token");
        String openid = (String) accessMap.get("openid");

        //3、拿到access_token 和 oppenid，再去请求微信提供固定的API，获取到扫码人的信息
        //TODO 查询数据库当前用用户是否曾经使用过微信登录

        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", openid));

        if (memberEntity == null) {
            System.out.println("新用户注册");
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            //发送请求
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //解析json
            HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            String nickName = (String) userInfoMap.get("nickname");      //昵称
            Double sex = (Double) userInfoMap.get("sex");        //性别
            String headimgurl = (String) userInfoMap.get("headimgurl");      //微信头像

            //把扫码人的信息添加到数据库中
            memberEntity = new MemberEntity();
            memberEntity.setNickname(nickName);
            memberEntity.setGender(Integer.valueOf(Double.valueOf(sex).intValue()));
            memberEntity.setHeader(headimgurl);
            memberEntity.setCreateTime(new Date());
            memberEntity.setSocialUid(openid);
            // register.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.insert(memberEntity);
        }
        return memberEntity;
    }

    /**
     * 微博社交登录（登录和注册功能合并）
     */
    @Override
    public MemberEntity login(WBSocialUserTO user) throws Exception {
        // 1.判断当前用户是否已经在本系统注册
        String uid = user.getUid();
        MemberEntity _entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("weibo_uid", user.getUid()));
        if (_entity != null) {
            // 2.已注册，直接返回
            MemberEntity member = new MemberEntity();
            member.setId(_entity.getId());
            member.setAccessToken(user.getAccessToken());
            member.setExpiresIn(user.getExpiresIn());
            baseMapper.updateById(member);
            // 返回
            _entity.setAccessToken(user.getAccessToken());
            _entity.setExpiresIn(user.getExpiresIn());
            return _entity;
        } else {
            // 3.未注册
            MemberEntity member = new MemberEntity();
            try {
                // 查询当前社交用户的社交账号信息，封装会员信息（查询结果不影响注册结果，所以使用try/catch）
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("access_token", user.getAccessToken());
                queryMap.put("uid", user.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", new HashMap<String, String>(), queryMap);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String profileImageUrl = jsonObject.getString("profile_image_url");
                    // 封装注册信息
                    member.setNickname(name);
                    member.setGender("m".equals(gender) ? 1 : 0);
                    member.setHeader(profileImageUrl);
                    member.setCreateTime(new Date());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            member.setSocialUid(user.getUid());
            member.setAccessToken(user.getAccessToken());
            member.setExpiresIn(user.getExpiresIn());
            //把用户信息插入到数据库中
            baseMapper.insert(member);
            return member;
        }
    }

    /**
     * 注册
     */
    @Override
    public void regist(MemberUserRegisterTO user) throws InterruptedException {
        // 1.加锁
        RLock lock = redissonClient.getLock(MemberConstant.LOCK_KEY_REGIST_PRE + user.getPhone());
        try {
            lock.tryLock(30L, TimeUnit.SECONDS);
            // 2.校验
            // 校验手机号唯一、用户名唯一
            checkPhoneUnique(user.getPhone());
            checkUserNameUnique(user.getUserName());
            // 3.封装保存
            MemberEntity entity = new MemberEntity();
            entity.setUsername(user.getUserName());
            entity.setMobile(user.getPhone());
            entity.setNickname(user.getUserName());
            // 3.1.设置默认等级信息
            MemberLevelEntity level = memberLevelService.getDefaultLevel();
            entity.setLevelId(level.getId());
            // 3.2.设置密码加密存储
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encode = passwordEncoder.encode(user.getPassword());
            entity.setPassword(encode);
            entity.setCreateTime(new Date());
            this.baseMapper.insert(entity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existUserByUsername(String username) {
        return baseMapper.exists(new QueryWrapper<MemberEntity>().eq("username", username));
    }

    @Override
    public boolean existUserByMobile(String mobile) {
        return baseMapper.exists(new QueryWrapper<MemberEntity>().eq("mobile", mobile));
    }

    @Override
    public boolean updateMemberInfo(Long id, MemberEntity member) {
        member.setId(id);
        return this.baseMapper.updateById(member) > 0;
    }

}