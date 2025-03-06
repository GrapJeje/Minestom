package nl.grapjeje.minestom.Listeners.Player;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.TimeUnit;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Ball;
import nl.grapjeje.minestom.Util.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerEntityInteractListener implements EventListener<PlayerEntityInteractEvent> {
    private static final List<Player> playersOnCooldown = new ArrayList<>();

    @Override
    public void run(PlayerEntityInteractEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getTarget();

        if (playersOnCooldown.contains(player)) return;
        if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;

        Ball.instance.kick(player, this.getKickPower(player));
        player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is getrapt! (Power: ")
                .append(Text.getColoredMessage(TextColor.fromHexString("#D48341"), String.valueOf(this.getKickPower(player)))
                        .append(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), ")"))));

        var scheduler = MinecraftServer.getSchedulerManager();

        playersOnCooldown.add(player);

        scheduler.buildTask(() ->
                playersOnCooldown.remove(player))
                .delay(10, TimeUnit.SERVER_TICK).schedule();
    }

    private float getKickPower(Player player) {
        final Random random = new Random();

        if (player == null) return random.nextFloat(0.1f, 10);

        return player.isSprinting() ? random.nextFloat(5, 10)
                : player.isSneaking() ? random.nextFloat(0.1f, 5)
                : random.nextFloat(0.1f, 10);
    }
}