package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import nl.grapjeje.minestom.Listeners.EventListener;

public class PlayerJoinListener implements EventListener<AsyncPlayerConfigurationEvent> {

    @Override
    public void run(AsyncPlayerConfigurationEvent e) {
        Player player = e.getPlayer();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        e.setSpawningInstance(instanceContainer);
        player.setRespawnPoint(new Pos(0, 40, 0));
    }
}
