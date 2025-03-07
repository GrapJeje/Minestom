package nl.grapjeje.minestom.Listeners.Player;

import net.minestom.server.event.entity.EntityTickEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Entity.BallEntity;

public class PlayerEntityCollideListener implements EventListener<EntityTickEvent> {

    @Override
    public void run(EntityTickEvent e) {
        if (e.getEntity() instanceof BallEntity) {
            ((BallEntity) e.getEntity()).checkForCollisions();
        }
    }
}
