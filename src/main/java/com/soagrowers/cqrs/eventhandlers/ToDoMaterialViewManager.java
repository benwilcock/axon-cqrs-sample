package com.soagrowers.cqrs.eventhandlers;

import com.soagrowers.cqrs.MaterialView;
import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

/**
 * Created by Ben on 10/08/2015.
 */
public class ToDoMaterialViewManager {

    @EventHandler
    public void handle(ToDoItemCreatedEvent event) {
        System.out.println("MaterialView: ToDoItemCreated: (" + event.getTodoId() + ") " + event.getDescription());
        MaterialView.getInstance().addToDo(event.getTodoId(), event.getDescription());
    }

    @EventHandler
    public void handle(ToDoItemCompletedEvent event) {
        System.out.println("MaterialView: ToDoItemCompleted: (" + event.getTodoId() + ")");
        MaterialView.getInstance().changeToDone(event.getTodoId());
    }
}
