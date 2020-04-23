package pl.memexurer.memebans.bans.mute;

import java.sql.ResultSet;
import java.util.UUID;

public class PlayerMute {
    private String mutedBy;
    private long mutedAt;
    private long unmuteAt;
    private String muteReason;
    private String playerName;
    private UUID playerUniqueId;

    private boolean needInsert;
    private boolean needDelete;

    public PlayerMute(String playerName, UUID playerUniqueId, String mutedBy, long mutedAt, long unmutteAt, String muteReason) {
        this.mutedBy = mutedBy;
        this.mutedAt = mutedAt;
        this.unmuteAt = unmutteAt;
        this.muteReason = muteReason;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.needInsert = true;
    }

    public PlayerMute(ResultSet set) {
        try {
            this.mutedBy = set.getString("MutedBy");
            this.mutedAt = set.getLong("MutedAt");
            this.unmuteAt = set.getLong("UnmuteAt");
            this.muteReason = set.getString("MuteReason");
            this.playerUniqueId = UUID.fromString(set.getString("PlayerUniqueId"));
            this.playerName = set.getString("PlayerName");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getMutedBy() {
        return mutedBy;
    }

    public long getMutedAt() {
        return mutedAt;
    }

    public long getUnmuteAt() {
        return unmuteAt;
    }

    public boolean isMuted() {
        if(System.currentTimeMillis() > unmuteAt && !needDelete) {
            needDelete = true;
            return true;
        } else return false;
    }

    public String getMuteReason() {
        return muteReason;
    }


    public boolean isNeedInsert() {
        return needInsert;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isNeedDelete() {
        return needDelete;
    }

    public void setDelete(boolean b) {
        this.needDelete = b;
    }
}
