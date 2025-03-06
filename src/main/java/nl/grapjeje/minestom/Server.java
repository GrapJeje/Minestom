package nl.grapjeje.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Listeners.Player.PlayerJoinListener;
import nl.grapjeje.minestom.Listeners.Player.PlayerSkinListener;
import org.jetbrains.annotations.NotNull;

public class Server {
    public MinecraftServer getServer() {
        return MinecraftServer.init();
    }

    public static InstanceManager manager;
    public static InstanceContainer container;

    public void start() {
        MinecraftServer server = this.getServer();
        manager = MinecraftServer.getInstanceManager();
        container = manager.createInstanceContainer();

        ServerSetup setup = new ServerSetup();
        setup.registerGeneration();
        setup.registerListeners();

        MojangAuth.init();
        server.start("0.0.0.0", 25565);
    }
}

class ServerSetup {
    public GlobalEventHandler eventHandler;

    public void registerListeners() {
        eventHandler = MinecraftServer.getGlobalEventHandler();

        this.registerListener(AsyncPlayerConfigurationEvent.class, new PlayerJoinListener());
        this.registerListener(PlayerSkinInitEvent.class, new PlayerSkinListener());
    }

    private <E extends Event> void registerListener(@NotNull Class<E> eventType, @NotNull EventListener<E> listener) {
        eventHandler.addListener(eventType, event -> listener.toConsumer().accept(event));
    }

    public void registerGeneration() {
        Server.container.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        Server.container.setChunkSupplier(LightingChunk::new);
    }
}