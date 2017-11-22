package no.fint.events

import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import spock.lang.Specification

import java.util.concurrent.*

class EventDispatcherSpec extends Specification {
    private EventDispatcher eventDispatcher
    private BlockingQueue<Event> queue

    void setup() {
        queue = new SynchronousQueue<Event>()
        eventDispatcher = new EventDispatcher(queue)
    }

    def "incoming event gets dispatched to event listener"() {
        given:
        def latch = new CountDownLatch(1)
        eventDispatcher.addListener('rfk.no', { event -> latch.countDown() } as EventListener)

        when:
        Executors.newSingleThreadExecutor().execute(eventDispatcher)
        queue.put(new Event('rfk.no', 'test-source', DefaultActions.HEALTH, 'test-client'))

        then:
        latch.await(2, TimeUnit.SECONDS)
    }
}
