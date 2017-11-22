package no.fint.events;

import com.hazelcast.core.HazelcastInstance;
import no.fint.event.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;

@Configuration
public class FintEventsConfig {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Bean
    public FintEvents fintEvents() {
        return new FintEvents(downstreamEventDispatcher(), upstreamEventDispatcher());
    }

    @Bean
    public EventDispatcher downstreamEventDispatcher() {
        return new EventDispatcher(downstreamQueue());
    }

    @Bean
    public EventDispatcher upstreamEventDispatcher() {
        return new EventDispatcher(upstreamQueue());
    }

    @Bean
    public BlockingQueue<Event> downstreamQueue() {
        return hazelcastInstance.getQueue(QueueType.DOWNSTREAM.getQueueName());
    }

    @Bean
    public BlockingQueue<Event> upstreamQueue() {
        return hazelcastInstance.getQueue(QueueType.UPSTREAM.getQueueName());
    }
}
