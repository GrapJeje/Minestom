package nl.grapjeje.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.*;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityTickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.Block;
import nl.grapjeje.minestom.Commands.*;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Listeners.Player.*;
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
        setup.registerCommands();

        MojangAuth.init();
        server.start("0.0.0.0", 25565);
    }
}

class ServerSetup {
    public GlobalEventHandler eventHandler;

    public void registerListeners() {
        eventHandler = MinecraftServer.getGlobalEventHandler();

        this.registerListener(EntityAttackEvent.class, new PlayerEntityHitListener());
        this.registerListener(PlayerEntityInteractEvent.class, new PlayerEntityInteractListener());
        this.registerListener(AsyncPlayerConfigurationEvent.class, new PlayerJoinListener());
        this.registerListener(PlayerSkinInitEvent.class, new PlayerSkinListener());
    }

    private <E extends Event> void registerListener(@NotNull Class<E> eventType, @NotNull EventListener<E> listener) {

        eventHandler.addListener(eventType, event -> listener.toConsumer().accept(event));
    }

    public void registerCommands() {
        this.registerCommand(new BallCommand());
        this.registerCommand(new Fly());
        this.registerCommand(new Gamemode());
    }

    private void registerCommand(Command command) {
        MinecraftServer.getCommandManager().register(command);
    }

    public void registerGeneration() {
        Server.container.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        Server.container.setChunkSupplier(LightingChunk::new);
    }
}