package nl.grapjeje.minestom.Model.Entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import java.util.List;

public interface BallEntity {
    Entity getInteractionEntity();

    void setVelocity(Entity ballEntity, Player kicker, float power, boolean vertical);

    void kick(Entity ballEntity, Player kicker);

    void kick(Entity ballEntity, Player kicker, float power);

    /**
     * Calculates the power of the kick based on the player's state.
     *
     * @param kicker The player who kicked the ball.
     * @return The power of the kick.
     */
    float getKickPower(Player kicker);

    void collide(Player player);

    void checkForCollisions();

    /**
     * Finds a ball instance based on the given entity.
     *
     * @param entity The entity to search for.
     * @return The found BallEntity instance, or null if not found.
     */
    static BallEntity findBallInstance(Entity entity) {
        return BallBehavior.ballEntities.stream()
                .filter(BallBehavior.class::isInstance)
                .map(BallBehavior.class::cast)
                .filter(ball -> ball.equals(entity) || ball.getInteractionEntity().equals(entity))
                .findFirst()
                .orElse(null);
    }
}