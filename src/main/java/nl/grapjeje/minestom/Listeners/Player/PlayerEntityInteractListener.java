package nl.grapjeje.minestom.Listeners.Player;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.*;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import nl.grapjeje.minestom.Listeners.EventListener;
import nl.grapjeje.minestom.Model.Ball;
import nl.grapjeje.minestom.Util.Text;

import java.util.Random;

public class PlayerEntityInteractListener implements EventListener<PlayerEntityInteractEvent> {

    @Override
    public void run(PlayerEntityInteractEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getTarget();

        if (Ball.Cooldown.isPlayerOnCooldown(player)) return;
        if (entity.getEntityType() != EntityType.FALLING_BLOCK) return;
        if (entity.getVelocity().y() > player.getVelocity().y()) return;

        Ball.instance.kick(player, this.getKickPower(player));
        player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is getrapt! (Power: ")
                .append(Text.getColoredMessage(TextColor.fromHexString("#D48341"), String.valueOf(this.getKickPower(player)))
                        .append(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), ")"))));
    }

    private float getKickPower(Player player) {
        final Random random = new Random();

        if (player == null) return random.nextFloat(0.1f, 10);

        return player.isSprinting() ? random.nextFloat(5, 10)
                : player.isSneaking() ? random.nextFloat(0.1f, 5)
                : random.nextFloat(0.1f, 10);
    }
}