package nl.grapjeje.minestom.Commands;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import nl.grapjeje.minestom.Model.Entity.BallBehavior;
import nl.grapjeje.minestom.Util.Text;

public class BallCommand extends Command {

    public BallCommand() {
        super("ball");

        this.setDefaultExecutor((sender, context) -> {
            Player player = (Player) sender;

            BallBehavior.instance = new BallBehavior(player.getPosition());

            player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Bal is verschenen!"));
        });
    }
}