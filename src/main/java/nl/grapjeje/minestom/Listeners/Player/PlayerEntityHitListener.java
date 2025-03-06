package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Ball;

public class PlayerEntityHitListener implements EventListener<EntityAttackEvent> {

    @Override
    public void run(EntityAttackEvent e) {
        Entity damagedEntity = e.getTarget();
        if (damagedEntity.getEntityType() != EntityType.FALLING_BLOCK) return;

        Entity damager = e.getEntity();
        if (!(damager instanceof Player player)) return;

        if (Ball.Cooldown.isPlayerOnCooldown(player)) return;
        if (damagedEntity.getVelocity().y() > player.getVelocity().y() + 1) return;

        Ball.instance.kick(player, Ball.instance.getKickPower(player));
    }
}