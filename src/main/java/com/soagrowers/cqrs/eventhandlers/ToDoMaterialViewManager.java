package com.soagrowers.cqrs.eventhandlers;

import com.soagrowers.cqrs.views.MaterialView;
import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

/**
 * Created by Ben on 10/08/2015.
 */
public class ToDoMaterialViewManager {

    @EventHandler
    public void handle(ToDoItemCreatedEvent event) {
        System.out.println("MaterialView: ToDoItemCreated: (" + event.getToDoId() + ") " + event.getDescription());
        MaterialView.getInstance().addToDo(event.getToDoId(), event.getDescription());
    }

    @EventHandler
    public void handle(ToDoItemCompletedEvent event) {
        System.out.println("MaterialView: ToDoItemCompleted: (" + event.getToDoId() + ")");
        MaterialView.getInstance().changeToDone(event.getToDoId());
    }
}
