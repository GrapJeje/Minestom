package nl.grapjeje.minestom.Listeners;

import net.minestom.server.event.Event;

import java.util.function.Consumer;

public interface EventListener<T extends Event> {

    void run(T e);

    default Consumer<T> toConsumer() {
        return this::run;
    }
}
