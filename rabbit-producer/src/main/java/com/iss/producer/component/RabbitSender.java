package com.iss.producer.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 生产者
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 这里是确认消息的回调监听接口，用于确认消息是否被broker所收到，消息生产者消息回调
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        /**
         *
         * @param correlationData 作为一个Rabbit服务节点和生产者之间传递信息的唯一标识
         * @param ack  标识Rabbit服务节点消息是否被收到
         * @param cause  失败的一些异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("消息ACK结果：" + ack + ", correlationData：" + correlationData.getId());
        }
    };

    /**
     * 对外发送消息的方法
     *
     * @param message    具体的消息内容
     * @param properties 额外的附加属性
     * @throws Exception 抛出的异常
     */
    public void send(Object message, Map<String, Object> properties) throws Exception {
        //消息头，可以天添加额外的属性
        MessageHeaders mhs = new MessageHeaders(properties);
        //通过消息头、消息体创建消息
        Message<?> msg = MessageBuilder.createMessage(message, mhs);
        //设置消息回调监听
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //生成消费端和rabbit服务端之间消息交互的唯一标识
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().trim());
        // 同步方法（没发送完消息，这个方法一直处于线程阻塞状态），生产者消息发送完以后回调这个方法
        MessagePostProcessor mpp = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                System.err.println("---> post to do ： " + message);
                return message;
            }
        };

        /**
         * 总共有四个参数
         * exchange ：表示要发送到目的地的交换机
         * routingKey：生产者指定路由键
         * message：发送的消息实体
         * messagePostProcessor：消息发送完的回调
         * correlationData：生产者和rabbitmq节点之间传递消息的唯一标识
         */
        rabbitTemplate.convertAndSend("exchange-1",
                "springboot.rabbit",
                msg,
                mpp,
                correlationData);

    }
}
