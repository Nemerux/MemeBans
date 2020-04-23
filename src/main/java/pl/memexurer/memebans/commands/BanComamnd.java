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

import java.util.Arrays;
import java.util.stream.Collectors;

public class BanComamnd implements CommandExecutor {
    private PlayerBanData banData;
    private PluginConfiguration configuration;

    public BanComamnd(PluginConfiguration configuration, PlayerBanData banData) {
        this.banData = banData;
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("memebans.ban")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /ban (nick) (czas, daj byle co jezeli nie chcesz tego uzywac) (powod)");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Ten gracz nigdy nie gral na serwerze!");
            return true;
        }

        long time = DateUtils.parseDateDiff(args[1], true);
        String reason = Arrays.stream(args, 2, args.length).collect(Collectors.joining(" "));

        PlayerBan ban = banData.banPlayer(player, time, reason, sender.getName(), null);
        Bukkit.broadcastMessage(configuration.BAN_BROADCAST_MESSAGE.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{BAN_TIME}", "{UNBAN_TIME}", "{REASON}"}, player.getName(), ban.getBannedBy(), ban.getBannedAt() + "", ban.getUnbannedAt() + "", ban.getBanReason()));
        return true;
    }
}
