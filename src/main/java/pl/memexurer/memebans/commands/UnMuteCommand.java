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
import java.util.Optional;
import java.util.stream.Collectors;

public class UnMuteCommand implements CommandExecutor {
    private PluginConfiguration configuration;
    private PlayerMuteData muteData;

    public UnMuteCommand(PluginConfiguration configuration, PlayerMuteData muteData) {
        this.configuration = configuration;
        this.muteData = muteData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("memebans.mute")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /unmute (nick)");
            return true;
        }

        Optional<PlayerMute> mute = muteData.findByName(args[0]);
        if(!mute.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Ten gracz nie jest wyciszony!");
            return true;
        }

        mute.get().setDelete(true);
        Bukkit.broadcastMessage(configuration.UNMUTE_MESSAGE.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{MUTE_TIME}", "{UNMUTE_TIME}", "{REASON}"}, mute.get().getPlayerName(), sender.getName(), DateUtils.formatDate(mute.get().getMutedAt()), DateUtils.formatDate(mute.get().getUnmuteAt()), mute.get().getMuteReason()));
        return true;
    }
}
