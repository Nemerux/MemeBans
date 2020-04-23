package pl.memexurer.memebans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.memexurer.memebans.bans.ban.PlayerBan;
import pl.memexurer.memebans.bans.ban.PlayerBanData;
import pl.memexurer.memebans.config.impl.PluginConfiguration;
import pl.memexurer.memebans.util.DateUtils;

import java.util.Optional;

public class UnBanCommand implements CommandExecutor {
    private PlayerBanData banData;
    private PluginConfiguration configuration;

    public UnBanCommand(PluginConfiguration configuration, PlayerBanData banData) {
        this.banData = banData;
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("memebans.ban")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /unban (nick)");
            return true;
        }

        Optional<PlayerBan> ban = banData.findByName(args[0]);
        if(!ban.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Ten gracz nie jest zbanowany!");
            return true;
        }

        banData.removeBan(ban.get());
        Bukkit.broadcastMessage(configuration.UNBAN_MESSAGE.getFormattedMessage(new String[] {"{PLAYER}", "{ADMIN}", "{BAN_TIME}", "{UNBAN_TIME}", "{REASON}"}, ban.get().getPlayerName(), sender.getName(), DateUtils.formatDate(ban.get().getBannedAt()), DateUtils.formatDate(ban.get().getUnbannedAt()), ban.get().getBanReason()));
        return true;
    }
}
