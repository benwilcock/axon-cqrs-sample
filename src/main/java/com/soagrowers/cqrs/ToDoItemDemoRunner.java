package com.soagrowers.cqrs;

import com.soagrowers.cqrs.commands.CreateToDoItemCommand;
import com.soagrowers.cqrs.commands.MarkCompletedCommand;
import com.soagrowers.cqrs.views.MaterialView;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWork;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ben on 07/08/2015.
 */
public class ToDoItemDemoRunner {

    private static CommandGateway commandGateway;
    private static EventSourcingRepository<ToDoItem> repository;


    public ToDoItemDemoRunner() {}

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("sampleContext.xml");
        commandGateway = applicationContext.getBean(CommandGateway.class);
        repository = (EventSourcingRepository<ToDoItem>) applicationContext.getBean("toDoRepository");

        ToDoItemDemoRunner runner = new ToDoItemDemoRunner();
        runner.run();

        TimeUnit.SECONDS.sleep(1l);
        System.exit(0);
    }

    public void run() {

        /**
         * Finally, lets add some Command and Event subscribers to the Buses (a.k.a handlers)
         */

        // Register our EventListener's with Axon...
        //AnnotationEventListenerAdapter.subscribe(new ToDoEventConsoleLoggingHandler(), eventBus);
        // For example: And event listener that updates a material view could be added.
        //AnnotationEventListenerAdapter.subscribe(new ToDoMaterialViewManager(), eventBus);

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
