package com.soagrowers.cqrs.events;

/**
 * Created by Ben on 07/08/2015.
 */

public class ToDoItemCompletedEvent extends AbstractToDoItemChangedEvent {

  public ToDoItemCompletedEvent(String todoId) {
    super(todoId);
  }
}
