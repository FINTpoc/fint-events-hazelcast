package no.fint.events;

import no.fint.event.model.Event;

import java.util.function.Consumer;

public interface EventListener extends Consumer<Event> {
}
