package pl.memexurer.memebans.bans.ban;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.memexurer.database.PluginDatabaseConnection;
import pl.memexurer.memebans.config.impl.PluginConfiguration;
import pl.memexurer.memebans.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlayerBanData {
    private final PluginConfiguration configuration;
    private final PluginDatabaseConnection databaseConnection;
    private final HashMap<UUID, PlayerBan> banMap;

    public PlayerBanData(PluginConfiguration configuration, PluginDatabaseConnection connection) {
        this.databaseConnection = connection;
        this.banMap = new HashMap<>();
        this.configuration = configuration;
    }

    public void load() {
        databaseConnection.update("CREATE TABLE IF NOT EXISTS `bans` (BannedBy varchar(16), BannedAt bigint(12), UnbannedAt bigint(12), BanReason varchar(128), BannedIp varchar(16), PlayerUniqueId varchar(36), PlayerName varchar(16));");
        ResultSet set = databaseConnection.query("SELECT * FROM `bans`");
        while (true) {
            try {
                if (!set.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            PlayerBan ban = new PlayerBan(set);
            banMap.put(ban.getPlayerUniqueId(), ban);
        }
    }

    public void save() {
        try {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO `bans` VALUES (?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement deleteStmt = databaseConnection.getConnection().prepareStatement("DELETE FROM `mutes` WHERE PlayerUniqueId=?");

            for (PlayerBan ban : banMap.values()) {
                if (ban.isNeedInsert()) {
                    stmt.setString(1, ban.getBannedBy());
                    stmt.setLong(2, ban.getBannedAt());
                    stmt.setLong(3, ban.getUnbannedAt());
                    stmt.setString(4, ban.getBanReason());
                    stmt.setString(5, ban.getBannedIp());
                    stmt.setString(6, ban.getPlayerUniqueId().toString());
                    stmt.setString(7, ban.getPlayerName());
                    stmt.addBatch();
                } else if (ban.isNeedDelete()) {
                    banMap.remove(ban.getPlayerUniqueId());
                    deleteStmt.setString(1, ban.getPlayerUniqueId().toString());
                    deleteStmt.addBatch();
                }
            }

            stmt.executeBatch();
            deleteStmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<PlayerBan> getBan(OfflinePlayer player) {
        return Optional.ofNullable(banMap.get(player.getUniqueId()));
    }

    public Optional<PlayerBan> findByName(String playerName) {
        for (PlayerBan ban : banMap.values())
            if (ban.getPlayerName().equalsIgnoreCase(playerName))
                return Optional.of(ban);
        return Optional.empty();
    }

    public void removeBan(PlayerBan ban) {
        ban.setDelete(true);
    }

    public PlayerBan banPlayer(OfflinePlayer player, long time, String reason, String bannedBy, String ip) {
        PlayerBan ban = new PlayerBan(player.getName(), player.getUniqueId(), bannedBy, System.currentTimeMillis(), time, reason, ip);
        banMap.put(player.getUniqueId(), ban);
        if (player.isOnline())
            ((Player) player).kickPlayer(configuration.BAN_FORMAT.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{BAN_TIME}", "{UNBAN_TIME}", "{REASON}"}, player.getName(), bannedBy, DateUtils.formatDate(System.currentTimeMillis()), DateUtils.formatDate(time), reason));
        return ban;
    }
}
