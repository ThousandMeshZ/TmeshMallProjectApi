package com.tmesh.common.vo.member;

import lombok.Data;

@Data
public class MemberReceiveAddressVO {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * 收货人姓名
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮政编码
     */
    private String postCode;
    /**
     * 省份/直辖市
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区
     */
    private String region;
    /**
     * 所在地区
     */
    private String area;
    /**
     * 详细地址(街道)
     */
    private String detail;
    /**
     * 省市区代码
     */
    private String areacode;
    /**
     * 是否默认
     */
    private Integer defaultStatus;

    /**
     * table 用来判断是不是默认
     */
    private boolean LAY_CHECKED;
}
