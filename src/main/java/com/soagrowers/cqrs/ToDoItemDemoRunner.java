package com.soagrowers.cqrs;

import com.mongodb.Mongo;
import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.eventhandlers.ToDoEventConsoleLoggingHandler;
import com.soagrowers.cqrs.eventhandlers.ToDoMaterialViewManager;
import com.soagrowers.cqrs.views.MaterialView;
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
import org.axonframework.eventstore.mongo.DefaultMongoTemplate;
import org.axonframework.eventstore.mongo.MongoEventStore;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWork;

import java.io.File;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemDemoRunner {

    private static final String MONGO_HOST = "127.0.0.1";
    private static final Integer MONGO_PORT = 27017;
    private static final String MONGO_CQRS_DB = "cqrs";
    private static final String MONGO_EVENTS_COLLECTION = "events";
    private static final String MONGO_SNAPSHOTS_COLLECTION = "snapshots";
    private static final String MONGO_USERNAME = "";
    private static final char[] MONGO_PASSWORD = new char[0];


    public static void main(String[] args) throws InterruptedException {

        /**
         * First things first, lets setup some basic COMMAND Handling infrastucture
         * using Axon...
         */

        // To start with, instantiate a simple CommandBus and EventBus
        CommandBus commandBus = new SimpleCommandBus();

        // The command gateway simplifies working with the CommandBus
        CommandGateway commandGateway = new DefaultCommandGateway(commandBus);

        /**
         * Next, lets setup some EVENT Handling and EVENT Storage infrastructure...
         */

        // Instantiate a Mongo EventStore
        Mongo mongo = null;
        try {
            mongo = new Mongo(MONGO_HOST, MONGO_PORT);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Clear down database from previous runs.
        mongo.getDB(MONGO_CQRS_DB).getCollection(MONGO_EVENTS_COLLECTION).drop();
        mongo.getDB(MONGO_CQRS_DB).getCollection(MONGO_SNAPSHOTS_COLLECTION).drop();

        EventStore eventStore = new MongoEventStore(new DefaultMongoTemplate(mongo, MONGO_CQRS_DB, MONGO_EVENTS_COLLECTION, MONGO_SNAPSHOTS_COLLECTION, MONGO_USERNAME, MONGO_PASSWORD));

        // Instantiate a simple EventBus (this will propagate events to event handlers)
        EventBus eventBus = new SimpleEventBus();

        // Wire the EventStore and the EventBus together to provide an 'Aggregate Repository' for ToDoItem's
        EventSourcingRepository repository = new EventSourcingRepository(ToDoItem.class, eventStore);
        repository.setEventBus(eventBus);

        /**
         * Finally, lets add some Command and Event subscribers to the Buses (a.k.a handlers)
         */

        // Axon needs to know that our ToDoItem Aggregate can handle 'commands'
        AggregateAnnotationCommandHandler.subscribe(ToDoItem.class, repository, commandBus);

        // Register our EventListener's with Axon...
        AnnotationEventListenerAdapter.subscribe(new ToDoEventConsoleLoggingHandler(), eventBus);
        // For example: And event listener that updates a material view could be added.
        AnnotationEventListenerAdapter.subscribe(new ToDoMaterialViewManager(), eventBus);

        /**
         * Now lets Demonstrate Commands triggering events and Events being dealt with
         */

        // Create a unique id for our ToDoItem task...
        final String toDoItemId = UUID.randomUUID().toString();

        // Create a ToDoItem by sending a COMMAND to the CommandGateway...
        CreateToDoItemCommand newToDoItemCommand = new CreateToDoItemCommand(toDoItemId, "Buy milk.");
        System.out.println("First Command > 'CreateToDoItem'");
        System.out.println("Command: 'CreateToDoItem' sending...");
        commandGateway.send(newToDoItemCommand);
        System.out.println("Now look in the event store ('./events/ToDoItem).");
        MaterialView.getInstance().dumpView();

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

        System.out.println("Let's re-load the aggregate from the store...");
        loadAggregate(repository, toDoItemId);
        System.out.println("Notice how the event was 're-applied' to the aggregate by the repository");

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
        System.out.println("Second Command > 'MarkCompleted'");
        System.out.println("Command: 'MarkCompleted' sending...");
        commandGateway.send(completedToDoItemCommand);
        MaterialView.getInstance().dumpView();

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

        System.out.println("Re-load the aggregate a second time...");
        loadAggregate(repository, toDoItemId);
        System.out.println("This time 2 events were 're-applied' to the aggregate by the repository");
        System.out.println("and it's state becomes 'completed'.");

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

        System.out.println("\n------------------- EVENT REPOSITORY LOAD -----------------------");
        System.out.println("Loading Aggregate 'ToDoItem' with Id: " + toDoItemId);
        System.out.println("Events Re-applied...");
        UnitOfWork unitOfWork = new DefaultUnitOfWorkFactory().createUnitOfWork();
        ToDoItem item = (ToDoItem) repository.load(toDoItemId);
        unitOfWork.commit();
        System.out.println("Loaded Aggregate...");
        System.out.println("ToDoItem (" + item.getId() + ") " + "'" + item.getDescription() + "' " + "Complete?: " + item.isComplete());
        System.out.println("----------------------------------------------------------------\n");
    }
}
