package com.tmesh.common.vo.member;

import lombok.Data;

/**
 * @Description: 社交用户信息
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
