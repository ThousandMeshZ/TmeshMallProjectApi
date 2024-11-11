package com.tmesh.common.vo.ware;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: TMesh
 **/

@Data
public class FareVO {
    private MemberAddressVO address;
    private BigDecimal fare;
}


