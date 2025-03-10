package nl.grapjeje.minestom.Model.Entity;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.*;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Server;
import nl.grapjeje.minestom.Util.Text;

import java.util.*;

public class BallBehavior extends Entity implements BallEntity {
    public static final List<BallEntity> ballEntities = new ArrayList<>();
    private final Entity interactionEntity;
    private boolean isKicked = false;

    public BallBehavior(Pos position, Instance instance) {
        super(EntityType.BLOCK_DISPLAY);
        this.setInstance(instance, position);

        Vec scale = new Vec(0.5f, 0.5f, 0.5f);

        interactionEntity = new Entity(EntityType.INTERACTION);
        interactionEntity.setInstance(instance, position);

        float width = (float) scale.x() * 0.5f;
        float height = (float) scale.y() * 0.5f;
        float depth = (float) scale.z() * 0.5f;
        interactionEntity.setBoundingBox(width, height, depth);

        // Configure the BlockDisplay
        BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setBlockState(Block.STONE);
        meta.setScale(scale);

        this.setNoGravity(false);
        this.setBoundingBox(width, height, depth);

        ballEntities.add(this);
    }

    @Override
    public Entity getInteractionEntity() {
        return interactionEntity;
    }

    @Override
    public void setVelocity(Entity ballEntity, Player kicker, float power, boolean vertical) {
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
        direction = new Vec(direction.x(), direction.y(), direction.z()).normalize();

        float horizontalForce = power * 2.4f;
        float verticalForce = vertical ? 1.6f : 0.1f;

        // Apply the force to the ball
        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);
        this.setVelocity(force);
        interactionEntity.setVelocity(force);

        this.setNoGravity(true);
        MinecraftServer.getSchedulerManager().buildTask(() -> this.setNoGravity(false))
                .delay(1, TimeUnit.SECOND).schedule();
    }

    @Override
    public void kick(Entity ballEntity, Player kicker) {
        this.kick(ballEntity, kicker, this.getKickPower(kicker));
    }

    @Override
    public void kick(Entity ballEntity, Player kicker, float power) {
        if (Cooldown.isPlayerOnCooldown(kicker)) {
            kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c0bf1d"), "Je moet even wachten voordat je weer kunt schoppen!"));
            return;
        }

        Cooldown.addCooldown(kicker);

        // Check if the ball is above the player
        if (ballEntity.getPosition().y() > kicker.getPosition().y() + 0.8f) {
            kicker.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c0bf1d"), "Je benen zijn te kort"));
            return;
        }

        isKicked = true;
        this.setVelocity(ballEntity, kicker, power, true);
    }

    @Override
    public float getKickPower(Player kicker) {
        final Random random = new Random();

        if (kicker == null) return random.nextFloat(5, 8);

        return kicker.isSprinting() ? random.nextFloat(7, 10)
                : kicker.isSneaking() ? random.nextFloat(4, 5)
                : random.nextFloat(5, 8);
    }

    @Override
    public void collide(Player player) {
        this.setVelocity(this, player, 4, false);
    }

    @Override
    public void checkForCollisions() {
        Instance instance = this.getInstance();
        if (instance == null) return;

        BoundingBox ballBoundingBox = this.getBoundingBox();
        Pos ballPosition = this.getPosition();

        for (Entity entity : instance.getEntities()) {
            if (!(entity instanceof Player)) continue;

            BoundingBox playerBoundingBox = entity.getBoundingBox();
            Pos playerPosition = entity.getPosition();

            Point relativePosition = playerPosition.sub(ballPosition);

            // Check if the bounding boxes intersect
            if (ballBoundingBox.intersectBox(relativePosition, playerBoundingBox)) {
                this.collide((Player) entity);
            }
        }
    }

    // Physics constants
    private final double gravity = 0.08;
    private final double airResistance = 0.01;
    private final double friction = 0.05;
    private final double speedMultiplier = 1.05;
    private final double bounceFactor = 1.5;
    private final double rotationSpeed = 1.6;

    public void updateVelocity() {
        Vec velocity = this.getVelocity();
        double velocityX = velocity.x();
        double velocityY = velocity.y();
        double velocityZ = velocity.z();

        // Add a small speed multiplier
        velocityX *= speedMultiplier;
        velocityZ *= speedMultiplier;

        // Add some air resistance
        velocityX *= (1 - airResistance);
        velocityZ *= (1 - airResistance);

        // Apply gravity
        velocityY -= gravity;

        this.setVelocity(new Vec(velocityX, velocityY, velocityZ));

        // Update the rotation of the ball
        this.updateRotation(velocityX, velocityZ);
    }

    public void updateRotation(double velocityX, double velocityZ) {
        // Calculate the rotation based on the velocity
        double rotationX = this.getPosition().pitch() + velocityZ * rotationSpeed;
        double rotationY = this.getPosition().yaw() + velocityX * rotationSpeed;

        // Normalize the rotation to keep it within 0-360 degrees
        rotationY = rotationY % 360;

        this.teleport(new Pos(this.getPosition().x(), this.getPosition().y(), this.getPosition().z(), (float) rotationY, 0));
    }

    public void onGround() {
        Vec velocity = this.getVelocity();
        double velocityX = velocity.x();
        double velocityY = velocity.y();
        double velocityZ = velocity.z();

        // Apply friction
        velocityX *= (1 - friction);
        velocityZ *= (1 - friction);

        // Apply bounce effect
        if (velocityY < 0 && isKicked) {
            velocityY = -velocityY * bounceFactor;
            isKicked = false;
        }

        this.setVelocity(new Vec(velocityX, velocityY, velocityZ));
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        // Update the velocity of the ball
        this.updateVelocity();

        // Check for collisions
        this.checkForCollisions();

        // Check if the ball is on the ground and apply friction + bounce
        if (this.isOnGround()) {
            this.onGround();
        }

        if (this.isRemoved()) {
            interactionEntity.remove();
            return;
        }

        // Sync the position of the interaction entity with the BlockDisplay
        interactionEntity.teleport(this.getPosition());
        interactionEntity.setVelocity(this.getVelocity());
    }

    /**
     * Cooldown system to prevent spamming the ball.
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