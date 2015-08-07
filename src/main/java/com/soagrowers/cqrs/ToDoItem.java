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
 * ToDoItem is essentially a DDD AggregateRoot (from the DDD concept). In event-sourced
 * systems, Aggregates are often stored and retreived using a 'Repository'. In the
 * simplest terms, Aggregates are the sum of their applied 'Events'.
 *
 * The Repository stores the aggregate's Events in an 'Event Store'. When an Aggregate
 * is re-loaded by the repository, the Repository re-applies all the stored events
 * to the aggregate thereby re-creating the logical state of the Aggregate.
 *
 * The ToDoItem Aggregate can handle and react to 'Commands', and when it reacts
 * to these commands it creates and 'applies' Events that represent the logical changes
 * to be made. These Events are also handled by the ToDoItem.
 *
 * Axon takes care of much of this via the CommandBus, EventBus and Repository.
 *
 * Axon delivers commands placed on the bus to the Aggregate. Axon supports the 'applying' of
 * Events to the Aggregate, and the handling of those events by the aggregate or any other
 * configured EventHandlers.
 *
 */
public class ToDoItem extends AbstractAnnotatedAggregateRoot {

    /**
     * Aggregates that are managed by Axon must have a unique identifier.
     * Strategies similar to GUID are often used. The annotation 'AggregateIdentifier'
     * identifies the id field as such.
     */
    @AggregateIdentifier
    private String id;

    private String description;
    private boolean isComplete = false;

    /**
     * This default constructor is used by the Repository to construct
     * a prototype ToDoItem. Events are then used to set properties
     * such as the ToDoItem's Id in order to make the Aggregate reflect
     * it's true logical state.
     */
    public ToDoItem() {
    }

    /**
     * This constructor is marked as a 'CommandHandler' for the
     * CreateToDoItemCommand. This command can be used to construct
     * new instances of the Aggregate. If successful a new ToDoItemCreatedEvent
     * is 'applied' to the aggregate using the Axon 'apply' method. The apply
     * method appears to also propagate the Event to any other registered
     * 'Event Listeners', who may take further action.
     * @param command
     */
    @CommandHandler
    public ToDoItem(CreateToDoItemCommand command){
        System.out.println("Command: 'CreateToDoItem' received.");
        System.out.println("Event: 'ToDoItemCreated' applying...");
        apply(new ToDoItemCreatedEvent(command.getTodoId(), command.getDescription()));
    }

    /**
     * This method is marked as an EventHandler and is therefore used by the Axon framework to
     * handle events of the specified type (ToDoItemCreatedEvent). The ToDoItemCreatedEvent can be
     * raised either by the constructor during ToDoItem(CreateToDoItemCommand) or by the
     * Repository when 're-loading' the aggregate.
     * @param event
     */
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
