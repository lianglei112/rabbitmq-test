package com.iss.consumer.component;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 消费者
 */
@Component
public class RabbitReceive {

    /**
     * 用来监听发送的消息
     * RabbitListener：该注解可以放到类上或者方法上，用来绑定某一个队列和交换器，用来指定队列和交换器的对应关系
     * RabbitHandler：用来做具体处理
     *
     * @param message 消息实体
     * @param channel 接受消息的信道
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1", durable = "true"),
            exchange = @Exchange(name = "exchange-1",
                    durable = "true",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*"
        )
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        //1、收到消息以后进行业务端消费处理
        System.out.println("-----------------------");
        System.out.println("消费消息" + message.getPayload());

        //2、处理成功之后 获取deliveryTag 并进行手工的ACK操作，因为我们配置文件配置的是 手工签收
        // spring.rabbitmq.listener.simple.acknowledge-mode=manual  在接收到生产者发送的消息以后，需要手动签收消息
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
    }
}
