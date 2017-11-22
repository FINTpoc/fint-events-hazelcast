package no.fint.events;

import lombok.Synchronized;
import no.fint.event.model.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class EventDispatcher implements Runnable {
    private final BlockingQueue<Event> queue;
    private final Map<String, EventListener> listeners = new HashMap<>();

    public EventDispatcher(BlockingQueue<Event> queue) {
        this.queue = queue;
    }

    @Synchronized
    public void addListener(String orgId, EventListener eventListener) {
        listeners.put(orgId, eventListener);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Event event = queue.take();
                EventListener eventListener = listeners.get(event.getOrgId());
                eventListener.accept(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
