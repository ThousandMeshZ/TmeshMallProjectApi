package com.tmesh.tmeshmall.order.web;

import com.tmesh.tmeshmall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @author: wan
 */
@Controller
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试页面环境是否搭建成功
     * order.tmesh.cn/
     */
    @GetMapping(value = "/{page}.html")
    public String listPage(@PathVariable("page") String page) {

        return page;
    }

    @ResponseBody
    @GetMapping(value = "/test/createOrder")
    public String createOrderTest() {

        //订单下单成功
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());

        //给MQ发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);

        return "ok";
    }

}
