package com.soagrowers.cqrs;

import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.events.handlers.ToDoEventConsoleLoggingHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.eventstore.fs.SimpleEventFileResolver;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkFactory;

import java.io.File;
import java.util.UUID;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemDemoRunner {

    public static void main(String[] args) {

        /**
         * SetUp COMMAND Handling Infrastucture
         */

        // To start with, instantiate a simple CommandBus and EventBus
        CommandBus commandBus = new SimpleCommandBus();

        // The command gateway simplifies working with the CommandBus
        CommandGateway commandGateway = new DefaultCommandGateway(commandBus);


        /**
         * SetUp EVENT Handling Infrastucture
         */

        // Instantiate an EventStore (this will write events out the the file system)
        EventStore eventStore = new FileSystemEventStore(new SimpleEventFileResolver(new File("./events")));

        // Instantiate a simple EventBus (this will propagate events to event handlers)
        EventBus eventBus = new SimpleEventBus();

        /**
         * SetUp an AGGREGATE REPOSITORY (combines an EVENT BUS and an EVENT STORE)
         */

        // Wire EventStore and EventBus to provide an 'Aggregate Repository' for ToDoItem's
        EventSourcingRepository repository = new EventSourcingRepository(ToDoItem.class, eventStore);
        repository.setEventBus(eventBus);

        // Axon needs to know that our ToDoItem Aggregate can handle 'commands'
        AggregateAnnotationCommandHandler.subscribe(ToDoItem.class, repository, commandBus);

        // Register an EventListener with Axon...
        AnnotationEventListenerAdapter.subscribe(new ToDoEventConsoleLoggingHandler(), eventBus);

        /**
         * Now lets Demonstrate Commands triggering events and Events being dealt with
         */

        // Create a unique id for our ToDoItem task...
        final String toDoItemId = UUID.randomUUID().toString();

        // Create a ToDoItem by sending a COMMAND to the CommandGateway...
        CreateToDoItemCommand newToDoItemCommand = new CreateToDoItemCommand(toDoItemId, "Buy milk.");
        commandGateway.send(newToDoItemCommand);


        /**
         * OUTCOMES...
         *
         * 1. The Create command has resulted in an Event (see the console)
         * 2. The Event has been stored against the aggregateId
         * 3. The Event has been sent to the Event Listeners
         *
         * Look in the file system ./events/ToDoItem folder. you should see a new event file with the ToDoItemId
         * (which is in the console window).
         *
         * This file should contain 1 events - 1x 'ToDoItemCreatedEvent' with details of what the event contained.
         *
         * Now lets re-load the Aggregate from the Repository...
         */

        loadAggregate(repository, toDoItemId);

        /**
         * The console shows the state of the aggregate once the known events have been re-applied.
         *
         * 1. The Aggregate has the right ID
         * 2. The Aggregate had the right description
         * 3. The aggregate has a status of 'Completed = false'
         *
         * Now lets mark the ToDoItem as 'Completed' by sending a second COMMAND to the command Gateway...
         */

        MarkCompletedCommand completedToDoItemCommand = new MarkCompletedCommand(toDoItemId);
        commandGateway.send(completedToDoItemCommand);

        /**
         * OUTCOMES...
         *
         * 1. The MarkCompleted command has resulted in an Event (see the console)
         * 2. The Event has been stored against the aggregateId
         * 3. The Event has been sent to the Event Listeners
         *
         * Look in the file system ./events/ToDoItem folder. you should see a new event file with the ToDoItemId
         * (which is in the console window).
         *
         * This file should contain 2 events - 1x 'ToDoItemCreatedEvent' and 1x 'ToDoItemMarkedCompletedEvent'
         * with details of what each event contained.
         *
         * Now lets re-load the Aggregate from the Repository...
         */

        loadAggregate(repository, toDoItemId);

        /**
         * The console now shows the final state of the aggregate once the Created an MarkCompleted
         * events have both been applied.
         *
         * 1. The Aggregate has the right ID
         * 2. The Aggregate had the right description
         * 3. The aggregate has a status of 'Completed = true'
         *
         */
    }

    private static void loadAggregate(EventSourcingRepository repository, String toDoItemId) {

        System.out.println("\n---- Loading ToDoItem Aggregate with Id: " + toDoItemId + "----");
        UnitOfWork unitOfWork = new DefaultUnitOfWorkFactory().createUnitOfWork();
        ToDoItem item = (ToDoItem)repository.load(toDoItemId);
        unitOfWork.commit();

        System.out.println("ToDoItem (" + item.getId() + ")");
        System.out.println("Description: " + item.getDescription());
        System.out.println("Complete: " + item.isComplete() + "\n");

    }
}
