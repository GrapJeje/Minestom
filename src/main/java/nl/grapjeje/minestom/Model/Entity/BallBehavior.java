package nl.grapjeje.minestom.Model.Entity;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Server;
import nl.grapjeje.minestom.Util.Text;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BallBehavior extends Entity implements BallEntity {
    public static BallEntity instance;

    public BallBehavior(Pos position) {
        super(EntityType.FALLING_BLOCK);

        Instance instance = Server.container;
        this.setInstance(instance, position);

        FallingBlockMeta meta = (FallingBlockMeta) this.getEntityMeta();

        // Settings
        meta.setBlock(Block.STONE);

        this.setNoGravity(false);
        this.setBoundingBox(.5f, .5f, .5f);
    }

    @Override
    public void kick(Entity ballEntity, Player kicker, float power) {
        // Calculate the power of the kick
        power = Math.max(0, Math.min(power, 10));

        Vec direction = kicker.getPosition().direction();
        direction = new Vec(direction.x(), 0, direction.z()).normalize();

        float horizontalForce = power * 1.2f;
        float verticalForce = power * 0.8f;

        // Apply the force to the ball
        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);
        ballEntity.setVelocity(force);

        System.out.println("Ball kicked with force: " + force);
        System.out.println("Ball position after kick: " + ballEntity.getPosition());

        kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is getrapt! (Power: ")
                .append(Text.getColoredMessage(TextColor.fromHexString("#D48341"), String.valueOf(power)))
                        .append(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), ")")));

        Cooldown.addCooldown(kicker);
    }

    /**
     * Get the power of the kick based on the player's state
     *
     * @param kicker The player who kicked the ball
     * @return The power of the kick
     */
    @Override
    public float getKickPower(Player kicker) {
        final Random random = new Random();

        if (kicker == null) return random.nextFloat(0.1f, 10);

        if (!kicker.isOnGround()) {
            ParticlePacket packet = new ParticlePacket(
                    Particle.CRIT,
                    false,
                    position.x(), position.y(), position.z(),
                    0.5f, 0.5f, 0.5f,
                    0.1f,
                    10
            );

            Server.container.getPlayers().forEach(player -> player.sendPacket(packet));
            kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c01200"), "Super schot!"));

            return 10;
        }

        return kicker.isSprinting() ? random.nextFloat(5, 10)
                : kicker.isSneaking() ? random.nextFloat(0.1f, 5)
                : random.nextFloat(0.1f, 10);
    }

    @Override
    public void collide(Entity ballEntity, Entity collidedWith) {

    }

    /**
     * Cooldown system to prevent spamming the ball
     */
    public static class Cooldown {
        private static final Set<Player> playersOnCooldown = new HashSet<>();

        public static void addCooldown(Player player) {
            playersOnCooldown.add(player);
            scheduleCooldownRemoval(player);
        }

        public static void removeCooldown(Player player) {
            playersOnCooldown.remove(player);
        }

        public static boolean isPlayerOnCooldown(Player player) {
            return playersOnCooldown.contains(player);
        }

        private static void scheduleCooldownRemoval(Player player) {
            MinecraftServer.getSchedulerManager().buildTask(() ->
                            removeCooldown(player))
                    .delay(10, TimeUnit.SERVER_TICK).schedule();
        }
    }
}
