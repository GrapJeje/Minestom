package nl.grapjeje.minestom.Commands;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import nl.grapjeje.minestom.Util.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Gamemode extends Command {
    public Gamemode() {
        super("gamemode", "gm");

        var gamemodeArg = ArgumentType.String("gamemode");

        this.setDefaultExecutor((sender, context) ->
                sender.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c01200"), "Da kan nie")));

        this.addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            String mode = context.get(gamemodeArg);
            GameMode gameMode = switch (mode.toLowerCase()) {
                case "creative", "1" -> GameMode.CREATIVE;
                case "survival", "0" -> GameMode.SURVIVAL;
                case "adventure", "2" -> GameMode.ADVENTURE;
                case "spectator", "3" -> GameMode.SPECTATOR;
                default -> null;
            };

            if (gameMode == null) {
                player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#c01200"), "Da kan nie"));
                return;
            }

            player.setGameMode(gameMode);
            player.sendMessage(Text.getColoredMessage(TextColor.fromHexString("#1FC077"), "Je gamemode is veranderd naar ")
                    .append(Text.getColoredMessage(TextColor.fromHexString("#D48341"), mode.toLowerCase())));
        }, gamemodeArg);
    }
}
