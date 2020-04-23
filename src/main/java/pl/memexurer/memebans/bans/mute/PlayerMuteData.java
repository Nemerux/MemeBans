package pl.memexurer.memebans.bans.mute;

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

public class PlayerMuteData {
    private final HashMap<UUID, PlayerMute> muteMap;
    private final PluginConfiguration configuration;
    private final PluginDatabaseConnection databaseConnection;

    public PlayerMuteData(PluginConfiguration configuration, PluginDatabaseConnection connection) {
        this.databaseConnection = connection;
        this.configuration = configuration;
        this.muteMap = new HashMap<>();
    }

    public void load() {
        databaseConnection.update("CREATE TABLE IF NOT EXISTS `mutes` (MutedBy varchar(16), MutedAt bigint(12), UnmuteAt bigint(12), MuteReason varchar(128), PlayerUniqueId varchar(36), PlayerName varchar(16));");
        ResultSet set = databaseConnection.query("SELECT * FROM `mutes`");
        while (true) {
            try {
                if (!set.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            PlayerMute mute = new PlayerMute(set);
            muteMap.put(mute.getPlayerUniqueId(), mute);
        }
    }

    public void save() {
        try {
            PreparedStatement stmt = databaseConnection.getConnection().prepareStatement("INSERT INTO `mutes` VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement deleteStmt = databaseConnection.getConnection().prepareStatement("DELETE FROM `mutes` WHERE PlayerUniqueId=?");

            for (PlayerMute mute : muteMap.values()) {
                if(mute.isNeedInsert()) {
                    stmt.setString(1, mute.getMutedBy());
                    stmt.setLong(2, mute.getMutedAt());
                    stmt.setLong(3, mute.getUnmuteAt());
                    stmt.setString(4, mute.getMuteReason());
                    stmt.setString(5, mute.getPlayerUniqueId().toString());
                    stmt.setString(6, mute.getPlayerName());
                    stmt.addBatch();
                } else if(mute.isNeedDelete()) {
                    muteMap.remove(mute.getPlayerUniqueId());
                    deleteStmt.setString(1, mute.getPlayerUniqueId().toString());
                    deleteStmt.addBatch();
                }
            }

            stmt.executeBatch();
            deleteStmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<PlayerMute> getMute(OfflinePlayer player) {
        return Optional.ofNullable(muteMap.get(player.getUniqueId()));
    }

    public Optional<PlayerMute> findByName(String playerName) {
        for (PlayerMute mute : muteMap.values())
            if (mute.getPlayerName().equalsIgnoreCase(playerName))
                return Optional.of(mute);
        return Optional.empty();
    }

    public PlayerMute mutePlayer(OfflinePlayer player, String mutedBy, long unmuteTime, String reason) {
        PlayerMute mute = new PlayerMute(player.getName(), player.getUniqueId(),mutedBy, System.currentTimeMillis(), unmuteTime, reason);
        this.muteMap.put(player.getUniqueId(), mute);
        if(player.isOnline()) ((Player) player).sendMessage(configuration.MUTE_FORMAT.getFormattedMessage(new String[]{"{PLAYER}", "{ADMIN}", "{MUTE_TIME}", "{UNMUTE_TIME}", "{REASON}"}, player.getName() ,mute.getMutedBy(), DateUtils.formatDate(mute.getMutedAt()) , DateUtils.formatDate(mute.getUnmuteAt()), mute.getMuteReason()));

        return mute;
    }
}
