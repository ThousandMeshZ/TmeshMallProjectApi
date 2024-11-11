package com.tmesh.tmeshmall.product.task;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: TmeshMallProjectApi
 * @BelongsPackage: com.tmesh.tmeshmall.product.task
 * @description  :   
 * @author       : TMesh
 * @version      : 1.0.0
 * @createTime   : 2024/06/18 21:55
 * @updateUser   : TMesh  
 * @updateTime   : 2024/06/18 21:55
 * @updateRemark : 说明本次修改内容 
 */
@Component
public class StartInit {
//
//    @Autowired   可以注入bean
//    ISysUserService userService;

    @PostConstruct
    public void init() throws InterruptedException {
    }
}
