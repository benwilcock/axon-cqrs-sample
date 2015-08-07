package com.soagrowers.cqrs;

import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItem extends AbstractAnnotatedAggregateRoot {

    @AggregateIdentifier
    private String id;

    private String description;
    private boolean isComplete = false;

    public ToDoItem() {
    }

    @CommandHandler
    public ToDoItem(CreateToDoItemCommand command){
        System.out.println("Command: 'CreateToDoItem' received.");
        System.out.println("Event: 'ToDoItemCreated' applying...");
        apply(new ToDoItemCreatedEvent(command.getTodoId(), command.getDescription()));
    }

    @EventHandler
    public void on(ToDoItemCreatedEvent event){
        this.id = event.getTodoId();
        this.description = event.getDescription();
        System.out.println("Event: 'ToDoItemCreated' '" + event.getDescription() + "' (" + event.getTodoId() + ") applied.");
    }

    @CommandHandler
    public void markCompleted(MarkCompletedCommand command){
        System.out.println("Command: 'MarkCompleted' received.");
        System.out.println("Event: 'ToDoItemCompleted' applying...");
        apply(new ToDoItemCompletedEvent(id));
    }

    @EventHandler
    public void on(ToDoItemCompletedEvent event){
        this.isComplete = true;
        System.out.println("Event: 'ToDoItemCompleted' " + this.getDescription() + " (completed = " + this.isComplete + ") applied.");
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isComplete() {
        return isComplete;
    }
}