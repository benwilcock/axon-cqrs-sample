package com.soagrowers.cqrs;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.views.MaterialView;
import org.axonframework.eventhandling.replay.ReplayingCluster;
import org.axonframework.eventstore.management.CriteriaBuilder;
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.*;

/**
 * Created by ben on 17/08/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:replayContext.xml"})
public class ReplayFromStoreTest {

    @Autowired
    private ApplicationContext context;
    private ReplayingCluster cluster;
    private MongoEventStore store;
    private Mongo mongo;
    private DefaultMongoTemplate mongoTemplate;
    private int numberOfToDoItems = 10;

    @Before
    public void setUp() {

        this.cluster = context.getBean(ReplayingCluster.class);
        this.store = context.getBean(MongoEventStore.class);
        this.mongo = context.getBean(Mongo.class);
        this.mongoTemplate = context.getBean(DefaultMongoTemplate.class);
        assertNotNull(this.context);
        assertNotNull(this.cluster);
        assertNotNull(this.store);
        assertNotNull(this.mongo);
        assertNotNull(this.mongoTemplate);

        mongoTemplate.domainEventCollection().remove(new BasicDBObject());
        mongoTemplate.snapshotEventCollection().remove(new BasicDBObject());

        for (int i = 1; i <= numberOfToDoItems; i++) {
            addToDoItem(i);
        }
    }

    private void addToDoItem(int i) {
        ToDoItem item = new ToDoItem(new CreateToDoItemCommand(String.valueOf(i), "ToDoItem Number " + i));
        item.markCompleted(new MarkCompletedCommand(String.valueOf(i)));
        store.appendEvents("test", item.getUncommittedEvents());
    }


    @Test
    public void testReplays() {
        assertFalse(this.cluster.isInReplayMode());
        this.cluster.startReplay();
        assertEquals(numberOfToDoItems, MaterialView.getInstance().getDoneCount());
    }

    @After
    public void checkLog() {

        //MaterialView.getInstance().dumpView();
    }
}
