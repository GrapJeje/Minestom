package nl.grapjeje.minestom.Model.Entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;

import static nl.grapjeje.minestom.Model.Entity.BallBehavior.ballEntities;

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

    static BallEntity findBallInstance(Entity entity) {
        System.out.println("Zoeken naar bal voor entity: " + entity.getEntityType());

        for (BallEntity ballEntity : ballEntities) {
            if (ballEntity instanceof BallBehavior ball) {
                if (ball.equals(entity) || ball.getInteractionEntity().equals(entity)) {
                    System.out.println("Match gevonden! Dit is een bal.");
                    return ball;
                }
            }
        }

        System.out.println("Geen match gevonden.");
        return null;
    }
}
