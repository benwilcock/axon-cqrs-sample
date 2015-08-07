package com.soagrowers.cqrs;

import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemTest {

    private FixtureConfiguration fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Fixtures.newGivenWhenThenFixture(ToDoItem.class);
    }

    @Test
    public void testCreateToDoItem() throws Exception {
        fixture.given()
                .when(new CreateToDoItemCommand("todo1", "need to implement the aggregate"))
                .expectEvents(new ToDoItemCreatedEvent("todo1", "need to implement the aggregate"));
    }

    @Test
    public void testMarkToDoItemAsCompleted() throws Exception {
        fixture.given(new ToDoItemCreatedEvent("todo1", "need to implement the aggregate"))
                .when(new MarkCompletedCommand("todo1"))
                .expectEvents(new ToDoItemCompletedEvent("todo1"));
    }
}
