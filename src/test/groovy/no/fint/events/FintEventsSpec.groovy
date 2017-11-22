package no.fint.events

import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = TestApplication)
class FintEventsSpec extends Specification {

    @Autowired
    private FintEvents fintEvents

    def "initialize fint events"() {
        expect:
        fintEvents != null
    }

    def "Register downstream listener and send event"() {
        given:
        def latch = new CountDownLatch(1)
        def event = new Event('rfk.no', 'test-source', DefaultActions.HEALTH, 'test-client')

        when:
        fintEvents.registerDownstreamListener('rfk.no', { e -> latch.countDown() } as EventListener)
        fintEvents.sendDownstream(event)

        then:
        latch.await(2, TimeUnit.SECONDS)
    }

    def "Register upstream listener and send event"() {
        given:
        def latch = new CountDownLatch(1)
        def event = new Event('rfk.no', 'test-source', DefaultActions.HEALTH, 'test-client')

        when:
        fintEvents.registerUpstreamListener('rfk.no', { e -> latch.countDown() } as EventListener)
        fintEvents.sendUpstream(event)

        then:
        latch.await(2, TimeUnit.SECONDS)
    }
}
