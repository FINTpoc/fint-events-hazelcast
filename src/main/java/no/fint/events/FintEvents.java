package no.fint.events;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FintEvents {

    private final EventDispatcher downstreamDispatcher;
    private final EventDispatcher upstreamDispatcher;

    public FintEvents(EventDispatcher downstreamDispatcher, EventDispatcher upstreamDispatcher) {
        this.downstreamDispatcher = downstreamDispatcher;
        this.upstreamDispatcher = upstreamDispatcher;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(downstreamDispatcher);
        executorService.execute(upstreamDispatcher);
        log.info("Started event dispatchers");
    }

    public void registerUpstreamListener(String orgId, EventListener eventListener) {
        upstreamDispatcher.registerListener(orgId, eventListener);
    }

    public void registerDownstreamListener(String orgId, EventListener eventListener) {
        downstreamDispatcher.registerListener(orgId, eventListener);
    }

    public boolean sendUpstream(Event event) {
        return upstreamDispatcher.send(event);
    }

    public boolean sendDownstream(Event event) {
        return downstreamDispatcher.send(event);
    }
}
