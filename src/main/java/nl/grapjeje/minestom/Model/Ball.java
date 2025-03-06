package nl.grapjeje.minestom.Model;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Server;
import nl.grapjeje.minestom.Util.Text;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Ball {
    public static Ball instance;
    private Entity blockDisplayEntity;

    public void spawn(Pos pos) {
        Instance instance = Server.container;

        blockDisplayEntity = new Entity(EntityType.FALLING_BLOCK);
        blockDisplayEntity.setInstance(instance, pos);

        FallingBlockMeta meta = (FallingBlockMeta) blockDisplayEntity.getEntityMeta();
        meta.setBlock(Block.STONE);
    }

    public void kick(@NotNull Player player, float power) {
        power = Math.max(0, Math.min(power, 10));

        Vec direction = player.getPosition().direction();
        direction = new Vec(direction.x(), 0, direction.z()).normalize();

        float horizontalForce = power * 1.2f;
        float verticalForce = power * 0.8f;

        Vec force = new Vec(direction.x() * horizontalForce, verticalForce, direction.z() * horizontalForce);
        blockDisplayEntity.setVelocity(force);

        player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is getrapt! (Power: ")
                .append(Text.getColoredMessage(TextColor.fromHexString("#D48341"), String.valueOf(this.getKickPower(player)))
                        .append(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), ")"))));

        Cooldown.addCooldown(player);
    }

    public float getKickPower(Player player) {
        final Random random = new Random();

        if (player == null) return random.nextFloat(0.1f, 10);

        return player.isSprinting() ? random.nextFloat(5, 10)
                : player.isSneaking() ? random.nextFloat(0.1f, 5)
                : random.nextFloat(0.1f, 10);
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