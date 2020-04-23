package pl.memexurer.memebans.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import pl.memexurer.memebans.bans.ban.PlayerBan;
import pl.memexurer.memebans.bans.ban.PlayerBanData;
import pl.memexurer.memebans.bans.mute.PlayerMute;
import pl.memexurer.memebans.bans.mute.PlayerMuteData;
import pl.memexurer.memebans.config.FormattedMessage;
import pl.memexurer.memebans.config.impl.PluginConfiguration;
import pl.memexurer.memebans.util.DateUtils;

import java.util.Optional;

public class PlayerActionListener implements Listener {
    private PluginConfiguration configuration;
    private PlayerBanData banData;
    private PlayerMuteData muteData;

    public PlayerActionListener(PluginConfiguration configuration, PlayerBanData banData, PlayerMuteData muteData) {
        this.banData = banData;
        this.muteData = muteData;
        this.configuration = configuration;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Optional<PlayerBan> ban = banData.getBan(e.getPlayer());
        if (!ban.isPresent()) return;

        if (ban.get().isBanned()) {
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, format(configuration.BAN_FORMAT, e.getPlayer(), ban.get()));

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("memebans.ban"))
                    p.sendMessage(format(configuration.BAN_JOIN_MESSAGE, e.getPlayer(), ban.get()));
            }
        }
    }

    private String format(FormattedMessage message, Player player, PlayerBan ban) {
        return message.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{BAN_TIME}", "{UNBAN_TIME}", "{REASON}"}, player.getName(), ban.getBannedBy(), DateUtils.formatDate(ban.getBannedAt()), DateUtils.formatDate(ban.getBannedAt()), ban.getBanReason());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Optional<PlayerMute> player = muteData.getMute(e.getPlayer());
        if (!player.isPresent()) return;

        if (player.get().isMuted()) {
            e.getPlayer().sendMessage(configuration.MUTE_FORMAT.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{MUTE_TIME}", "{UNMUTE_TIME}", "{REASON}", "{MESSAGE}"}, e.getPlayer().getName(), player.get().getMutedBy(), DateUtils.formatDate(player.get().getMutedAt()), DateUtils.formatDate(player.get().getMutedAt()), player.get().getMuteReason(), e.getMessage()));
            String message = configuration.MUTE_CHAT_MESSAGE.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{MUTE_TIME}", "{UNMUTE_TIME}", "{REASON}", "{MESSAGE}"}, e.getPlayer().getName(), player.get().getMutedBy(), DateUtils.formatDate(player.get().getMutedAt()), DateUtils.formatDate(player.get().getMutedAt()), player.get().getMuteReason(), e.getMessage());
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.hasPermission("memebans.mute") || p.equals(e.getPlayer()))
                    p.sendMessage(message);
            e.setCancelled(true);
        }
    }
}
