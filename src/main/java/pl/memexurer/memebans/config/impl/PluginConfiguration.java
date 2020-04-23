package pl.memexurer.memebans.config.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.memebans.config.ConfigurationSource;
import pl.memexurer.memebans.config.CustomConfiguration;
import pl.memexurer.memebans.config.FormattedMessage;

public class PluginConfiguration extends CustomConfiguration {
    public PluginConfiguration(JavaPlugin plugin) {
        super(plugin);
    }

    @ConfigurationSource(path = "database")
    public ConfigurationSection DATABASE_CREDENTIALS;

    @ConfigurationSource(path = "ban.message")
    public FormattedMessage BAN_BROADCAST_MESSAGE;

    @ConfigurationSource(path = "ban.format")
    public FormattedMessage BAN_FORMAT;

    @ConfigurationSource(path = "ban.join_message")
    public FormattedMessage BAN_JOIN_MESSAGE;

    @ConfigurationSource(path = "ban.unban")
    public FormattedMessage UNBAN_MESSAGE;

    @ConfigurationSource(path = "mute.message")
    public FormattedMessage MUTE_MESSAGE;

    @ConfigurationSource(path = "mute.format")
    public FormattedMessage MUTE_FORMAT;

    @ConfigurationSource(path = "mute.chat")
    public FormattedMessage MUTE_CHAT_MESSAGE;

    @ConfigurationSource(path = "mute.unmute")
    public FormattedMessage UNMUTE_MESSAGE;

}
