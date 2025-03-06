package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.entity.*;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Ball;

public class PlayerEntityInteractListener implements EventListener<PlayerEntityInteractEvent> {

    @Override
    public void run(PlayerEntityInteractEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getTarget();

        if (Ball.Cooldown.isPlayerOnCooldown(player)) return;
        if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;
        if (entity.getVelocity().y() > player.getVelocity().y() + 1) return;

        Ball.instance.kick(player, Ball.instance.getKickPower(player));
    }
}