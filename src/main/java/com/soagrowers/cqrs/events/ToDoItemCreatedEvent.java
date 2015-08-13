package com.soagrowers.cqrs.events;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemCreatedEvent extends AbstractToDoItemChangedEvent {


  private final String description;

  public ToDoItemCreatedEvent(String todoId, String description) {
    super(todoId);
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
