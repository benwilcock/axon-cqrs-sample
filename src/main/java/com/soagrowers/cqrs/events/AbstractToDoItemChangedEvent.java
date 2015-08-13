package com.soagrowers.cqrs.events;

import java.io.Serializable;

/**
 * Created by Ben on 13/08/2015.
 */
public abstract class AbstractToDoItemChangedEvent implements Serializable{

  private final String toDoId;

  public AbstractToDoItemChangedEvent(String toDoId) {
    this.toDoId = toDoId;
  }

  public String getToDoId() {
    return toDoId;
  }
}
