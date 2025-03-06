package nl.grapjeje.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Listeners.Player.PlayerJoinListener;
import org.jetbrains.annotations.NotNull;

public final class Main {
    public Main instance = this;

    public static void main(String[] args) {
        new Server().start();
    }
}

class Server {
    public MinecraftServer getServer() {
        return MinecraftServer.init();
    }

    public void start() {
        MinecraftServer server = this.getServer();

        ServerSetup setup = new ServerSetup();
        setup.registerGeneration();
        setup.registerListeners();

        server.start("0.0.0.0", 25565);
    }
}

class ServerSetup {
    public GlobalEventHandler eventHandler;

    public void registerGeneration() {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
    }

    public void registerListeners() {
        eventHandler = MinecraftServer.getGlobalEventHandler();

        this.registerListener(AsyncPlayerConfigurationEvent.class, new PlayerJoinListener());
    }

    private <E extends Event> void registerListener(@NotNull Class<E> eventType, @NotNull EventListener<E> listener) {
        eventHandler.addListener(eventType, event -> listener.toConsumer().accept(event));
    }
}
