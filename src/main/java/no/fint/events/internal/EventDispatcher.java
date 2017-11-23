package no.fint.events.internal;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.events.FintEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class EventDispatcher implements Runnable {
    private final BlockingQueue<Event> queue;
    private final Map<String, FintEventListener> listeners = new HashMap<>();
    private final ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean();

    public EventDispatcher(BlockingQueue<Event> queue) {
        this.queue = queue;
        this.executorService = Executors.newCachedThreadPool();
    }

    @Synchronized
    public void registerListener(String orgId, FintEventListener fintEventListener) {
        listeners.put(orgId, fintEventListener);
    }

    @Override
    public void run() {
        try {
            if (running.compareAndSet(false, true)) {
                dispatch();
            } else {
                log.debug("Already running");
            }
        } finally {
            running.set(false);
        }
    }

    private void dispatch() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final Event event = queue.take();
                log.trace("Event received: {}", event);
                FintEventListener fintEventListener = listeners.get(event.getOrgId());
                if (fintEventListener == null) {
                    log.error("No listener found for orgId: {} on queue: {}", event.getOrgId(), queue);
                } else {
                    executorService.execute(() -> fintEventListener.accept(event));
                }
            }
        } catch (HazelcastInstanceNotActiveException | InterruptedException ignore) {
        }
    }

    public boolean send(Event event) {
        return queue.offer(event);
    }

    @Synchronized
    public void clearListeners() {
        listeners.clear();
    }

    public boolean isRunning() {
        return running.get();
    }
}
