package com.soagrowers.cqrs.events;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemCreatedEvent {

    private final String todoId;
    private final String description;

    public ToDoItemCreatedEvent(String todoId, String description) {
        this.todoId = todoId;
        this.description = description;
    }

    public String getTodoId() {
        return todoId;
    }

    public String getDescription() {
        return description;
    }
}
