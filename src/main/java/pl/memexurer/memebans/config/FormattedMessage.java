package pl.memexurer.memebans.config;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.util.List;

public class FormattedMessage {
    private String message;

    public FormattedMessage(List<String> message) {
        this.message = ChatColor.translateAlternateColorCodes('&', String.join("\n", message));
    }

    public String getFormattedMessage(String[] a1, String... a2) {
        Validate.isTrue(a1.length == a2.length, "Array lengths must be equal");
        String copyMessage = message;

        for(int index = 0; index < a1.length; index++) {
            copyMessage = copyMessage.replace(a1[index], a2[index]);
        }

        return copyMessage;
    }
}
