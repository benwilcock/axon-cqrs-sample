package com.soagrowers.cqrs;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Ben on 07/08/2015.
 */
public class TestRunner {

    private static ConnectionFactory connectionFactory;
    private static AmqpTemplate template;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("testContext.xml");
        connectionFactory = (ConnectionFactory) applicationContext.getBean("amqpConnectionFactory");
        template = (AmqpTemplate) applicationContext.getBean("amqpTemplate");
        template.convertAndSend("Ben.Temp.Queue", "foo");
        ((CachingConnectionFactory) connectionFactory).destroy();
        System.out.println("done.");
        //System.exit();
    }
}
