package com.iss.producer.test;

import com.iss.producer.component.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    /**
     * 装配生产者
     */
    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSend() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("attr1", "12345");
        properties.put("attr2", "aaadd");
        rabbitSender.send("hello rabbitmq!", properties);

        Thread.sleep(1000);
    }

}
