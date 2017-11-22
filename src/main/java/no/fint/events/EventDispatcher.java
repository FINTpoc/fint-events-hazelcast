package no.fint.events;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class EventDispatcher implements Runnable {
    private final BlockingQueue<Event> queue;
    private final Map<String, EventListener> listeners = new HashMap<>();
    private final ExecutorService executorService;

    public EventDispatcher(BlockingQueue<Event> queue) {
        this.queue = queue;
        this.executorService = Executors.newCachedThreadPool();
    }

    @Synchronized
    public void registerListener(String orgId, EventListener eventListener) {
        listeners.put(orgId, eventListener);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Event event = queue.take();
                log.debug("Event received: {}", event);
                EventListener eventListener = listeners.get(event.getOrgId());
                executorService.execute(() -> eventListener.accept(event));
            } catch (HazelcastInstanceNotActiveException |InterruptedException ignore) {
                return;
            }
        }
    }

    public boolean send(Event event) {
        return queue.offer(event);
    }
}
