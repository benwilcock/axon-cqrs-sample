package com.soagrowers.cqrs.events;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemCompletedEvent {

    private final String todoId;

    public ToDoItemCompletedEvent(String todoId) {
        this.todoId = todoId;
    }

    public String getTodoId() {
        return todoId;
    }
}
