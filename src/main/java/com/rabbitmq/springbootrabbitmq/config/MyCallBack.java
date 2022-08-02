package com.rabbitmq.springbootrabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private  RabbitTemplate rabbitTemplate;
    //注入
    @PostConstruct//声明周期的注解，当Autowired注入后自动调用
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }
    /**
     * 交换机确认回调方法
     * 1.发消息 交换机接收到了回调
     * 1.1correlationData 保存回调消息的ID及相关信息
     * 1.2交换机收到消息 ack= true
     * 1.3cause null
     * 2.发消息 交换机接收失败
     * 2.1correlationData 保存相关的ID及信息
     * 2.2ack = false
     * 2.3 cause 失败的原因
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
       String id =  correlationData != null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到ID为:{}的消息",id);
        }else{
            log.info("交换机还未收到Id为:{}的消息，由于原因:{}",id,cause);
        }
    }
    //可以在当消息传递过程中不可达到目的地时将消息返回给生产者
    //只有不可达目的地的时候才进行回退
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("消息{},被交换机{}退回,退回原因:{},路由Key:{}",
                new String(returned.getMessage().getBody()),returned.getExchange(),
                returned.getReplyText(),returned.getRoutingKey());
    }


}
