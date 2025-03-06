package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Main;
import nl.grapjeje.minestom.Server;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements EventListener<AsyncPlayerConfigurationEvent> {

    @Override
    public void run(AsyncPlayerConfigurationEvent e) {
        Player player = e.getPlayer();

        this.setRespawnPoint(e, player);
    }

    public void setRespawnPoint(@NotNull AsyncPlayerConfigurationEvent e, @NotNull Player player) {
        e.setSpawningInstance(Server.container);
        player.setRespawnPoint(new Pos(0, 40, 0));
    }
}
