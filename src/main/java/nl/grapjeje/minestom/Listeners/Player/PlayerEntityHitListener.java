package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Entity.BallBehavior;
import nl.grapjeje.minestom.Model.Entity.BallEntity;

public class PlayerEntityHitListener implements EventListener<EntityAttackEvent> {

    @Override
    public void run(EntityAttackEvent e) {
        Entity damagedEntity = e.getTarget();
        if (!(damagedEntity instanceof BallEntity)) return;

        Entity damager = e.getEntity();
        if (!(damager instanceof Player player)) return;

        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (BallBehavior.Cooldown.isPlayerOnCooldown(player)) return;

        BallBehavior.instance.kick(damagedEntity, player);
    }
}