package nl.grapjeje.minestom.Model;

import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Server;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class Ball {
    public static Ball instance;
    private Entity blockDisplayEntity;

    public void spawn(Pos pos) {
        Instance instance = Server.container;

        blockDisplayEntity = new Entity(EntityType.FALLING_BLOCK);
        blockDisplayEntity.setInstance(instance, pos);

        BlockDisplayMeta meta = (BlockDisplayMeta) blockDisplayEntity.getEntityMeta();
        meta.setBlockState(Block.STONE);
    }

    public void kick(@NotNull Player player, float power) {
        power = Math.max(0, Math.min(power, 10));

        Vec direction = player.getPosition().direction();
        direction = new Vec(direction.x(), 0, direction.z()).normalize();

        float horizontalForce = power * 1.2f;
        float verticalForce = power * 0.8f;

        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);
        blockDisplayEntity.setVelocity(force);

        Cooldown.addCooldown(player);
    }

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