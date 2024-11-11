package com.tmesh.tmeshmall.order.listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.tmesh.tmeshmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 定时关单，监听死信队列
 * @Author: TMesh
 * @Date: 2024/1/3 17:24
 */
@Slf4j
@RabbitListener(queues = "order.release.order.queue")
@Component
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void handleOrderRelease(com.tmesh.common.entity.order.OrderEntity commonOrder, Message message, Channel channel) throws IOException {
        OrderEntity order = new OrderEntity();
        BeanUtils.copyProperties(commonOrder, order);
        log.debug("订单解锁，订单号：" + order.getOrderSn());
        try {
            orderService.closeOrder(order);
            // 手动删除消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除订单报错", e);
            // 解锁失败 将消息重新放回队列，让别人消费
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}