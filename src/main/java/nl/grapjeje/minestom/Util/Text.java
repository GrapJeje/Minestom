package nl.grapjeje.minestom.Util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Text {

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Component getColoredMessage(TextColor color, String message) {
        return Component.text(message, color);
    }
}
