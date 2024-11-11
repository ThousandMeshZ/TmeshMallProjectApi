package com.tmesh.tmeshmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tmesh.tmeshmall.order.entity.OrderEntity;
import com.tmesh.tmeshmall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tmesh.common.utils.PageUtils;
import com.tmesh.common.utils.Query;

import com.tmesh.tmeshmall.order.dao.OrderItemDao;
import com.tmesh.tmeshmall.order.entity.OrderItemEntity;
import com.tmesh.tmeshmall.order.service.OrderItemService;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }
    
    /**
     * queues：声明需要监听的队列  
     * 
     * org.springframework.amqp.core.Message
     * 
     * 参数可以写一下类型
     * 1、Message message: 原生消息详细信息。头+体
     * 2、T<发送的消息的类型》OrderReturnReasonEntity content;
     * channel：当前传输数据的通道
     * Queue：可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     * 场景:
     *      1)、订单服务启动多个，同一个消息，只能有一个客户端收到
     *      2)、 只有一个消息完全处理完，方法运行结束后，我们就可以接到下一个消息
     * 获取实际消息内容有两种方式：
     *  方式一：在方法参数列表中直接声明出来
     *  方式二：从请求体中取出消息的二进制形式，然后通过JSON反序列化即可
     */
    //@RabbitListener(queues = {"hello-java-queue"})
    //@RabbitHandler
    public void revieveMessage(Message message, OrderReturnReasonEntity entity, Channel channel) {
        // 请求体，序列化存储（本例中已使用Jackson2JsonMessageConverter序列化器作JSON序列化存储）
        byte[] body = message.getBody();
        // 请求头
        MessageProperties messageProperties = message.getMessageProperties();
        // JSON反序列得到消息内容对象
        OrderReturnReasonEntity reason = JSONObject.parseObject(body, OrderReturnReasonEntity.class);
        System.out.println("接受到的消息对象" + message);
        System.out.println("接受到的消息内容" + reason);
        System.out.println("接受到的消息内容" + entity);
    }

    //@RabbitHandler
    public void revieveMessage(Message message, OrderEntity entity, Channel channel) {
        // 请求体，序列化存储（本例中已使用Jackson2JsonMessageConverter序列化器作JSON序列化存储）
        byte[] body = message.getBody();
        // 请求头
        MessageProperties properties = message.getMessageProperties();
        // channel内按顺序自增的long类型消息标签
        long deliveryTag = properties.getDeliveryTag();
        // JSON反序列得到消息内容对象
        OrderEntity reason = JSONObject.parseObject(body, OrderEntity.class);
        System.out.println("接受到的消息对象" + message);
        System.out.println("接受到的消息内容" + reason);
        System.out.println("接受到的消息实体" + entity);
        try {
            if (deliveryTag == 2) {
                // 手动确认，消息会从unacked中删除，total数量减1
                // boolean multiple：是否批量签收
                channel.basicAck(deliveryTag, false);
            } else {
                // 手动拒签
                // boolean multiple：是否批量拒签
                // boolean requeue：当前拒签消息是否发回服务器重新入队
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (IOException e) {
            // 网络中断
            e.printStackTrace();
        }
    }
}