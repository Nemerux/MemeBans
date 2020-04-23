package pl.memexurer.memebans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.memexurer.memebans.bans.mute.PlayerMute;
import pl.memexurer.memebans.bans.mute.PlayerMuteData;
import pl.memexurer.memebans.config.impl.PluginConfiguration;
import pl.memexurer.memebans.util.DateUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MuteCommand implements CommandExecutor {
    private PlayerMuteData muteData;
    private PluginConfiguration configuration;

    public MuteCommand(PluginConfiguration configuration, PlayerMuteData muteData) {
        this.muteData = muteData;
        this.configuration = configuration;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("memebans.mute")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /mute (nick) (czas, daj byle co jezeli nie chcesz tego uzywac) (powod)");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Ten gracz nigdy nie gral na serwerze!");
            return true;
        }

        long time = DateUtils.parseDateDiff(args[1], true);
        String reason = Arrays.stream(args, 2, args.length).collect(Collectors.joining(" "));

        PlayerMute mute = muteData.mutePlayer(player, sender.getName(), time, reason);
        Bukkit.broadcastMessage(configuration.MUTE_MESSAGE.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{MUTE_TIME}", "{UNMUTE_TIME}", "{REASON}"}, player.getName(), sender.getName(), DateUtils.formatDate(mute.getMutedAt()), DateUtils.formatDate(mute.getUnmuteAt()), mute.getMuteReason()));
        return true;
    }
}
