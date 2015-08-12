# axon-cqrs-sample

@Before:

1. Install Java and check it's working for you.
2. Install Maven 3 and check it's working for you.
3. Install RabbitMq (http://localhost:15672) and add a 'test' user with password 'password'.
4. Install MongoDb (running locally against localhost needs no user or password)

@Test

run 'mvn exec:java'

If everything is hanging together you should see some console entries explaining what has happened.

@After:

Check out the code in ToDoItem.java and in ToDoItemDemoRunner.
Also, look at the code in the commands and events and eventhandlers packages.
Finally check out the spring configuration in axonContext.xml to see how it's all wired together.
Look in the mongodb 'cqrs' database's 'events' collection for your stored events. It should contain your event documents.
Look in rabbitMq console to see your message queue's metrics. It should show messages were moved through the exchange / queue.


