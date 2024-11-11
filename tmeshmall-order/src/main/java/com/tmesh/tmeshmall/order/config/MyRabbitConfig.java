package com.tmesh.tmeshmall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.amqp.core.Message;

/**
 * @Author: TMesh
 * @Date: 2023/12/15 0:04
 */
@Configuration
public class MyRabbitConfig {

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        // TODO 封装 RabbitTemplate
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate(rabbitTemplate);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        // 使用 json 序列化器来序列化消息，发送消息时，消息对象会被序列化成json格式
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制 RabbitTemplate
     * 1、服务收到消息就会回调
     *      1、spring.rabbitmq.publisher-confirms: true
     *      2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     *      1、spring.rabbitmq.publisher-returns: true
     *          spring.rabbitmq.template.mandatory: true
     *      2、设置确认回调ReturnCallback
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以broker删除这个消息)
     *      1、默认是自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除这个消息
     *          问题:
     *              我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机了。发生消息丢失。
     *              消费者手动确认模式。只要我们没有明确告诉MQ，货物被签收。没有Ack，消息就一直是unacked状态。
     *                  即使Consumer宕机，消息也不会丢失，会重新变回ready，下一次有新的Consumer连接进来就会发给他
     *      2、如何签收
     *           channel.basicAck(deliveryTag, false); 签收货物
     */
    //@PostConstruct   // (MyRabbitConfig对象创建完成以后，执行这个方法)
    public void initRabbitTemplate(RabbitTemplate rabbitTemplate) {
        /**
         * 发送消息触发 confirmCallback 回调
         * @param correlationData：当前消息的唯一关联数据（如果发送消息时未指定此值，则回调时返回null）
         * @param ack：消息是否成功收到（ack=true，消息抵达Broker）
         * @param cause：失败的原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("发送消息触发 confirmCallback 回调" +
                    "\ncorrelationData ===> " + correlationData +
                    "\nack ===> " + ack + "" +
                    "\ncause ===> " + cause);
            System.out.println("=================================================");
        });

        /**
         * 消息未到达队列触发 returnCallback 回调
         * 只要消息没有投递给指定的队列，就触发这个失败回调
         * @param message：投递失败的消息详细信息
         * @param replyCode：回复的状态码
         * @param replyText：回复的文本内容
         * @param exchange：接收消息的交换机
         * @param routingKey：接收消息的路由键
         */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returned) {
                System.out.println("消息未到达队列触发returnCallback回调" +
                        "\nmessage ===> " + returned.getMessage() +
                        "\nreplyCode ===> " + returned.getReplyCode() +
                        "\nreplyText ===> " + returned.getReplyText() +
                        "\nexchange ===> " + returned.getExchange() +
                        "\nroutingKey ===> " + returned.getRoutingKey());
                // TODO 修改mq_message，设置消息状态为2-错误抵达【后期定时器重发消息】
            }
        });
/*         rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("消息未到达队列触发returnCallback回调" +
                    "\nmessage ===> " + message +
                    "\nreplyCode ===> " + replyCode +
                    "\nreplyText ===> " + replyText +
                    "\nexchange ===> " + exchange +
                    "\nroutingKey ===> " + routingKey);
            // TODO 修改mq_message，设置消息状态为2-错误抵达【后期定时器重发消息】
        }); */
    }
    
   public class MyReturnedMessage implements RabbitTemplate.ReturnsCallback {
       @Override
       public void returnedMessage(ReturnedMessage returned) {
           System.out.println("消息未到达队列触发 returnCallback 回调" +
                   "\nmessage ===> " + returned.getMessage() +
                   "\nreplyCode ===> " + returned.getReplyCode() +
                   "\nreplyText ===> " + returned.getReplyText() +
                   "\nexchange ===> " + returned.getExchange() +
                   "\nroutingKey ===> " + returned.getRoutingKey());
           // TODO 修改mq_message，设置消息状态为2-错误抵达【后期定时器重发消息】
       }
   }
}