package nl.grapjeje.minestom.Model.Entity;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Server;
import nl.grapjeje.minestom.Util.Text;

import java.util.*;

public class BallBehavior extends Entity implements BallEntity {
    public static List<BallEntity> ballEntities = new ArrayList<>();
    private final Entity interactionEntity;
    private boolean isKicked = false;

    // The percentage of the velocity that is kept after a kick
    private final double gravity = 0.33;

    private Task velocityTask;
    private Task mainTask;

    public BallBehavior(Pos position, Instance instance) {
        super(EntityType.BLOCK_DISPLAY);

        this.setInstance(instance, position);

        interactionEntity = new Entity(EntityType.INTERACTION);
        interactionEntity.setInstance(instance, position);
        interactionEntity.setBoundingBox(.5f, .5f, .5f);

        BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setBlockState(Block.STONE);
        meta.setScale(new Vec(.5f, .5f, .5f));

        this.setNoGravity(false);
        this.setBoundingBox(.5f, .5f, .5f);

        mainTask = MinecraftServer.getSchedulerManager().buildTask(() -> {
            if (this.isRemoved()) {
                mainTask.cancel();
                interactionEntity.remove();
                return;
            }

            interactionEntity.setVelocity(this.getVelocity());
            this.checkForCollisions();
        }).repeat(1, TimeUnit.SERVER_TICK).schedule();
    }

    @Override
    public Entity getInteractionEntity() {
        return interactionEntity;
    }

    @Override
    public void setVelocity(Entity ballEntity, Player kicker, float power) {
        // Cancel the previous task so the ball doesn't get stuck
        if (velocityTask != null) velocityTask.cancel();

        // Calculate the power of the kick
        power = Math.max(0, Math.min(power, 10));

        if (!kicker.isOnGround() || power == 10) {
            Random random = new Random();
            float chance = random.nextFloat();

            // Get super kick chance
            if (chance < 0.33f || power == 10) {
                ParticlePacket packet = new ParticlePacket(
                        Particle.CRIT,
                        false,
                        position.x(), position.y(), position.z(),
                        0.5f, 0.5f, 0.5f,
                        0.1f,
                        10
                );

                Server.container.getPlayers().forEach(player -> player.sendPacket(packet));
                kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c0bf1d"), "Super schot!"));

                power = 10;
            }
        }

        Vec direction = kicker.getPosition().direction();
        direction = new Vec(direction.x(), 0, direction.z()).normalize();

        float horizontalForce = power * 2.4f;
        float verticalForce = power * 1.6f;

        // Apply the force to the ball
        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);

        this.setVelocity(force);
        interactionEntity.setVelocity(force);

        Vec finalDirection = direction;

        // Schedule the task to apply friction and bounce the ball
        velocityTask = MinecraftServer.getSchedulerManager().buildTask(() -> { // TODO: Make async
                    if (this.isOnGround() && isKicked) {
                        // Apply bounce effect
                        Vec newForce = new Vec(finalDirection.x() * horizontalForce * gravity,
                                verticalForce * gravity * 0.8,
                                finalDirection.z() * horizontalForce * gravity);

                        MinecraftServer.getSchedulerManager().buildTask(() -> {
                            this.setVelocity(Vec.ZERO);
                            interactionEntity.setVelocity(Vec.ZERO);
                            this.setVelocity(newForce);
                            interactionEntity.setVelocity(newForce);
                        });

                        isKicked = false;
                        velocityTask.cancel();
                    }
                })
                .repeat(1, TimeUnit.SERVER_TICK)
                .schedule();
    }

    @Override
    public void kick(Entity ballEntity, Player kicker) {
        this.kick(ballEntity, kicker, this.getKickPower(kicker));
    }

    @Override
    public void kick(Entity ballEntity, Player kicker, float power) {
        Cooldown.addCooldown(kicker);

        // Check if the ball is above the player
        if (ballEntity.getPosition().y() > kicker.getPosition().y() + .8f) {
            kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c0bf1d"), "Je benen zijn te kort"));
            return;
        }

        isKicked = true;
        this.setVelocity(ballEntity, kicker, power);
    }

    @Override
    public float getKickPower(Player kicker) {
        final Random random = new Random();

        if (kicker == null) return random.nextFloat(1f, 10);

        return kicker.isSprinting() ? random.nextFloat(5, 10)
                : kicker.isSneaking() ? random.nextFloat(1f, 5)
                : random.nextFloat(0.1f, 8);
    }

    @Override
    public void collide(Player player) {
        if (velocityTask != null) velocityTask.cancel();
        Vec direction = player.getPosition().direction();

        this.setVelocity(direction.mul(7f));
    }

    @Override
    public void checkForCollisions() {
        for (Entity entity : this.getInstance().getEntities()) {
            if (!(entity instanceof Player)) continue;
            Point relativePosition = entity.getPosition().sub(this.getPosition());

            // Check if the bounding boxes collide
            if (this.getBoundingBox().intersectBox(relativePosition, entity.getBoundingBox())) {
                this.collide((Player) entity);
            }
        }
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