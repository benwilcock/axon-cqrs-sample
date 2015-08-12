# axon-cqrs-sample

This sample is a simple demonstration of how to use the Java based [AxonFramework](http://www.axonframework.org) for CQRS/ES.
This demo aims to illustrates the following features of the platform...

- Command and Query Responsibility Separation (CQRS).
- Event Sourcing using an Aggregate Root based Repository
- Using MongoDb as your EventStore
- Publishing and Subscribing to distributed events using RabbitMQ

## @Before you start:

1. Install [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and check it's working for you.
2. Install [Maven](http://maven.apache.org/) and check it's working for you.
3. Install [RabbitMQ](https://www.rabbitmq.com/), go to the [Management Console](http://localhost:15672) and add a user called 'test' with a password of 'password'.
4. Install [MongoDb](https://www.mongodb.org/) (running locally against localhost requires no username or password)

## @Test the code:

To run the sample, either execute the `ToDoItemDemoRunner.java` class in your IDE or use Maven...

```
run 'mvn compile exec:java'
```

If everything is hanging together as intended, you should see a whole bunch console entries describing what has happened.

## @After you finish:

The `ToDoItemDemoRunner.java` demonstrates CQRS and EventSourcing using the Axon Framework. The `ToDoItem.java` class
models the `AggregateRoot` concept for a To-Do Item (Aggregate Root is a concept taken from Domain Driven Design).

The code in the `commands` & `events` packages model the commands an events from the business domain for this use case. As
you'll discover the `CreateToDoItemCommand` causes a `ToDoItemCreatedEvent` and the `MarkCompletedCommand` causes a
`ToDoItemCompletedEvent`. The handling of commands and the firing of events is handled by the `ToDoItem.java` aggregate root.

In Axon, commands are sent to a `CommandGateway` and delivered to the 'aggregate root' by Axon. Axon takes care of storing and publishing
any events that follow the command being handled using it's built-in 'EventRepository'. This EventRepository brings together
MongoDb as the database persistence mechanism and RabbitMQ as the Pub/Sub mechanism for events.The spring configuration
in `sampleContext.xml` describes how all the integrated components are wired together.

Once the demo has run, look in the mongodb 'cqrs' database's 'events' collection for your stored events. It should contain
your event documents. Likewise, look in the rabbitMq console to see your message queue's metrics. It should show that messages
were moved through the exchange / queue when the demo was run.

The `ToDoMaterialViewManager.java` is an event listener that manages the content of the simple materialised view in the
`MaterialisedView.java` class. In CQRS, materialised views are use as the basis of a simplified means of presenting content to users
and systems without the need to refer to the more complicated 'aggreegate root' model.


