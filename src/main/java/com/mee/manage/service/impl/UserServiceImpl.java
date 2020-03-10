package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mee.manage.config.Config;
import com.mee.manage.config.MeeConfig;
import com.mee.manage.mapper.UserMapper;
import com.mee.manage.po.User;
import com.mee.manage.service.IAuthenticationService;
import com.mee.manage.service.IUserService;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.JoddHttpUtils;
import com.mee.manage.vo.Yiyun.YiYunResponse;
import com.mee.manage.vo.Yiyun.YiyunUserData;
import com.mee.manage.vo.Yiyun.YiyunUserVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(IUserService.class);


    @Autowired
    IAuthenticationService authService;

    @Autowired
    Config config;

    @Override
    public User getUserByName(String name) {
        if(StringUtils.isEmpty(name))
            return null;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        User user = getOne(queryWrapper);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if(userId == null)
            return null;

        User user = getById(userId);
        return user;
    }

    @Override
    public List<YiyunUserData> getAllYiYunUser(String bizId) {
        return getYiyunUsers(bizId,"all");
    }

    @Override
    public YiyunUserData getYiyunUser(String bizId,String userId) {
        if(userId == null)
            return null;

        List<YiyunUserData> yiyunUsers = getYiyunUsers(bizId,userId);
        YiyunUserData userData = null;
        if(yiyunUsers != null && !yiyunUsers.isEmpty() ) {
            userData = yiyunUsers.get(0);
        }
        return userData;
    }

    private List<YiyunUserData> getYiyunUsers(String bizId,String userId) {
        String url = getMeeUrl(config.getBizUsersUrl(),bizId,userId);
        logger.info("Url:{}",url);

        String result = JoddHttpUtils.getData(url);
        if(result == null || result.isEmpty())
            return null;

        logger.info("result: {}",result);
        List<YiYunResponse<YiyunUserVo>> yiYunResponse = JSON.parseObject(result,new TypeReference<List<YiYunResponse<YiyunUserVo>>>(){});
        if(yiYunResponse == null || yiYunResponse.isEmpty())
            return null;

        List<YiyunUserData> userData = null;
        YiYunResponse<YiyunUserVo> yiYun = yiYunResponse.get(0);
        if(yiYun.getResult() != null && yiYun.getResult().equals("SUCCESS")) {
            YiyunUserVo userVo = yiYun.getData();
            if(userVo != null) {
                userData = userVo.getUserData();
            }
        }

        return userData;
    }


    public String getMeeUrl(String url,String bizId,String userId){
        Long time = DateUtil.getCurrentTime();
        String token = authService.getMeeToken(bizId);
        if(token == null)
            return null;

        String nonce = RandomStringUtils.random(6,true,false);
        String sign = MeeConfig.getMeeSign(bizId,userId,time,token,nonce);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/").append(bizId);
        if(!StringUtils.isEmpty(userId)) {
            stringBuilder.append("/" +userId);
        }
        stringBuilder.append("/").append(time);
        stringBuilder.append("/" + nonce + "/" + sign);
        url = url + stringBuilder.toString();
//        logger.info(url);
        return url;
    }


}
