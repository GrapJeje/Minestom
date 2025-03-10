package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.entity.*;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Entity.BallBehavior;
import nl.grapjeje.minestom.Model.Entity.BallEntity;

public class PlayerEntityInteractListener implements EventListener<PlayerEntityInteractEvent> {

    @Override
    public void run(PlayerEntityInteractEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getTarget();

        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (BallBehavior.Cooldown.isPlayerOnCooldown(player)) return;

        BallEntity ball = BallEntity.findBallInstance(entity);
        if (ball != null) ball.kick(entity, player);
    }
}