package com.soagrowers.cqrs;

import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.views.MaterialView;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWork;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;

/**
 * Created by Ben on 07/08/2015.
 */
public class TestRunner {

    private static final String MONGO_HOST = "server07";
    private static final Integer MONGO_PORT = 27017;
    private static final String MONGO_CQRS_DB = "cqrs";
    private static final String MONGO_EVENTS_COLLECTION = "events";
    private static final String MONGO_SNAPSHOTS_COLLECTION = "snapshots";
    private static final String MONGO_USERNAME = "";
    private static final char[] MONGO_PASSWORD = new char[0];

    private static CommandBus commandBus;
    private static EventBus eventBus;
    private static CommandGateway commandGateway;
    private static EventSourcingRepository<ToDoItem> repository;
    private static ConnectionFactory connectionFactory;
    private static AmqpTemplate template;


    public TestRunner() {
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("testContext.xml");
        connectionFactory = (ConnectionFactory)applicationContext.getBean("amqpConnectionFactory");
        template = (AmqpTemplate)applicationContext.getBean("amqpTemplate");
        TestRunner runner = new TestRunner();
        runner.run();
    }

    public void run() {
        template.convertAndSend("Ben.Temp.Queue", "foo");
    }
}
