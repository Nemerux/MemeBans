package pl.memexurer.memebans.bans.ban;

import pl.memexurer.memebans.MemeBansPlugin;

import java.sql.ResultSet;
import java.util.UUID;

public class PlayerBan {
    private String bannedBy;
    private long bannedAt;
    private long unbannedAt;
    private String banReason;
    private String bannedIp;
    private String playerName;
    private UUID playerUniqueId;

    private boolean needInsert;
    private boolean needDelete;

    public PlayerBan(String playerName, UUID playerUniqueId, String bannedBy, long bannedAt, long unbannedAt, String banReason, String isIpBanned) {
        this.bannedBy = bannedBy;
        this.bannedAt = bannedAt;
        this.unbannedAt = unbannedAt;
        this.banReason = banReason;
        this.bannedIp = isIpBanned;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;

        this.needInsert = true;
    }

    public PlayerBan(ResultSet set) {
        try {
            this.bannedBy = set.getString("BannedBy");
            this.bannedAt = set.getLong("BannedAt");
            this.unbannedAt = set.getLong("UnbannedAt");
            this.banReason = set.getString("BanReason");
            this.bannedIp = set.getString("BannedIp");
            this.playerUniqueId = UUID.fromString(set.getString("PlayerUniqueId"));
            this.playerName = set.getString("PlayerName");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isBanned() {
        if(System.currentTimeMillis() > unbannedAt && !needDelete) {
            needDelete = true;
            return true;
        } else return false;
    }

    public boolean isNeedDelete() {
        return needDelete;
    }

    public String getBannedIp() {
        return bannedIp;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public long getBannedAt() {
        return bannedAt;
    }

    public long getUnbannedAt() {
        return unbannedAt;
    }

    public String getBanReason() {
        return banReason;
    }
    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public boolean isNeedInsert() {
        return needInsert;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setDelete(boolean b) {
        this.needDelete = b;
    }
}
