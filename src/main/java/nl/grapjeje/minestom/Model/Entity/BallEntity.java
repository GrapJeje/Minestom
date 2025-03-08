package nl.grapjeje.minestom.Model.Entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

public interface BallEntity {
    Entity getInteractionEntity();

    void setVelocity(Entity ballEntity, Player kicker, float power);

    void kick(Entity ballEntity, Player kicker);

    void kick(Entity ballEntity, Player kicker, float power);

    /**
     * Get the power of the kick based on the player's state
     *
     * @param kicker The player who kicked the ball
     * @return The power of the kick
     */
    float getKickPower(Player kicker);

    void collide(Player player);

    void checkForCollisions();
}
