package com.soagrowers.cqrs.eventhandlers;

import com.soagrowers.cqrs.events.ToDoItemCompletedEvent;
import com.soagrowers.cqrs.events.ToDoItemCreatedEvent;
import com.soagrowers.cqrs.views.MaterialView;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.replay.ReplayAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ben on 10/08/2015.
 */
public class ToDoMaterialViewManager implements ReplayAware{

    private static final Logger log = LoggerFactory.getLogger(ToDoMaterialViewManager.class);

    @EventHandler
    public void handle(ToDoItemCreatedEvent event) {
        log.info("MaterialisedViewListener: ToDoItemCreated: (" + event.getToDoId() + ") " + event.getDescription());
        MaterialView.getInstance().addToDo(event.getToDoId(), event.getDescription());
    }

    @EventHandler
    public void handle(ToDoItemCompletedEvent event) {
        log.info("MaterialisedViewListener: ToDoItemCompleted: (" + event.getToDoId() + ")");
        MaterialView.getInstance().changeToDone(event.getToDoId());
    }

    public void beforeReplay() {
        log.info("Event Replay is about to START. Clearing the Materialised View.");
        MaterialView.getInstance().clearView();
    }

    public void afterReplay() {
        log.info("Notification has arrived that the replaying of events has FINISHED.");
    }

    public void onReplayFailed(Throwable cause) {
        log.error("Notification has arrived that the replaying of events has FAILED.");
    }
}
