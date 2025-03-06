package nl.grapjeje.minestom.Listeners.Player;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Ball;
import nl.grapjeje.minestom.Util.Text;

public class PlayerEntityInteractListener implements EventListener<PlayerEntityInteractEvent> {

    @Override
    public void run(PlayerEntityInteractEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getTarget();

        if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;

        Ball.instance.kick(player, 10);
        player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is getrapt!"));
    }
}