package pl.memexurer.memebans;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.database.PluginDatabaseConnection;
import pl.memexurer.memebans.bans.ban.PlayerBanData;
import pl.memexurer.memebans.bans.mute.PlayerMuteData;
import pl.memexurer.memebans.commands.BanComamnd;
import pl.memexurer.memebans.commands.MuteCommand;
import pl.memexurer.memebans.commands.UnBanCommand;
import pl.memexurer.memebans.commands.UnMuteCommand;
import pl.memexurer.memebans.config.impl.PluginConfiguration;
import pl.memexurer.memebans.listener.PlayerActionListener;

public final class MemeBansPlugin extends JavaPlugin {
    //NIE, nie uzywaj tego pluginu. Pobierz LiteBans (albo zakup?).
    //Mają tam połączenie ze stroną internetowa, i dzialaja lepiej niz to
    //Ale jak chcesz, ten plugin jest "autorski", i dziala. (chyba?)

    private static MemeBansPlugin PLUGIN_INSTANCE;
    private final PluginConfiguration pluginConfiguration = new PluginConfiguration(this);
    private PlayerBanData banData;
    private PlayerMuteData muteData;

    @Override
    public void onEnable() {
        MemeBansPlugin.PLUGIN_INSTANCE = this;
        this.pluginConfiguration.load();

        PluginDatabaseConnection databaseConnection = PluginDatabaseConnection.findDatabaseService(new DatabaseCredentials(pluginConfiguration.DATABASE_CREDENTIALS), this);

        this.banData = new PlayerBanData(pluginConfiguration, databaseConnection);
        this.banData.load();

        this.muteData = new PlayerMuteData(pluginConfiguration, databaseConnection);
        this.muteData.load();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            banData.save();
            muteData.save();
        }, 20L * 60L * 5L, 20L * 60L * 5L);

        Bukkit.getPluginManager().registerEvents(new PlayerActionListener(pluginConfiguration, banData, muteData), this);
        getCommand("ban").setExecutor(new BanComamnd(pluginConfiguration, banData));
        getCommand("mute").setExecutor(new MuteCommand(pluginConfiguration, muteData));
        getCommand("unban").setExecutor(new UnBanCommand(pluginConfiguration, banData));
        getCommand("unmute").setExecutor(new UnMuteCommand(pluginConfiguration, muteData));
    }

    @Override
    public void onDisable() {
        this.banData.save();
        this.muteData.save();
    }

    public static MemeBansPlugin getPluginInstance() {
        return PLUGIN_INSTANCE;
    }

    public PluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }
}
