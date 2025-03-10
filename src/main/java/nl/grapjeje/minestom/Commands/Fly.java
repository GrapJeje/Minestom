package nl.grapjeje.minestom.Commands;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import nl.grapjeje.minestom.Util.Text;

public class Fly extends Command {

    public Fly() {
        super("fly");

        this.setDefaultExecutor((sender, context) -> {
            Player player = (Player) sender;

            if (player.isAllowFlying()) {
                player.setFlying(false);
                player.setAllowFlying(false);
                player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Je kunt nu niet meer vliegen!"));
            } else {
                player.setFlying(true);
                player.setAllowFlying(true);
                player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Je kunt nu vliegen!"));
            }
        });
    }
}
