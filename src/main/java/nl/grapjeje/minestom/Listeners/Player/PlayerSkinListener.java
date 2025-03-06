package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import nl.grapjeje.minestom.Listeners.EventListener;

public class PlayerSkinListener implements EventListener<PlayerSkinInitEvent> {

    @Override
    public void run(PlayerSkinInitEvent e) {
        Player player = e.getPlayer();
        PlayerSkin skin = PlayerSkin.fromUuid(String.valueOf(player.getUuid()));
        player.setSkin(skin);
    }
}
