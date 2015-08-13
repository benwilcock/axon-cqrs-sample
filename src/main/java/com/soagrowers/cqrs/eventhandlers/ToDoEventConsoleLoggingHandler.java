package com.soagrowers.cqrs.eventhandlers;

import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

/**
 * Handler's (a.k.a. Listeners) can be used to react to events and perform associated
 * actions, such as updating a 'materialised-view' for example.
 */
public class ToDoEventConsoleLoggingHandler {

    @EventHandler
    public void handle(ToDoItemCreatedEvent event) {
        System.out.println("Listener: ToDoItemCreated: (" + event.getToDoId() + ") " + event.getDescription());
    }

    @EventHandler
    public void handle(ToDoItemCompletedEvent event) {
        System.out.println("Listener: ToDoItemCompleted: (" + event.getToDoId() + ")");
    }
}
