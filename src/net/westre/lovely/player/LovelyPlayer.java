/*
 * Decompiled with CFR 0_101.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package net.westre.lovely.player;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.westre.lovely.LovelyLinkable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LovelyPlayer
implements LovelyLinkable<UUID> {
    private OfflinePlayer offlinePlayer;
    private Player player;
    private int minutesPlayed;
    private boolean inPVPMode;

    public LovelyPlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public UUID getLinkedObject() {
        return this.offlinePlayer.getUniqueId();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getMinutesPlayed() {
        return this.minutesPlayed;
    }

    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public boolean isInPVPMode() {
        return this.inPVPMode;
    }

    public void setInPVPMode(boolean toggle) {
        this.inPVPMode = toggle;
    }

    public void updateDisplay() {
        String name = this.player.getName();
        if (this.getMinutesPlayed() >= 60 && this.getMinutesPlayed() < 300) {
            name = (Object)ChatColor.GOLD + "" + (Object)ChatColor.BOLD + "+" + (Object)ChatColor.RESET + this.player.getName();
        } else if (this.getMinutesPlayed() >= 300 && this.getMinutesPlayed() < 1200) {
            name = (Object)ChatColor.GOLD + "" + (Object)ChatColor.BOLD + "++" + (Object)ChatColor.RESET + this.player.getName();
        } else if (this.getMinutesPlayed() >= 1200 && this.getMinutesPlayed() < 6000) {
            name = (Object)ChatColor.GOLD + "" + (Object)ChatColor.BOLD + "+++" + (Object)ChatColor.RESET + this.player.getName();
        } else if (this.getMinutesPlayed() >= 6000) {
            name = (Object)ChatColor.GOLD + "" + (Object)ChatColor.BOLD + "++++" + (Object)ChatColor.RESET + this.player.getName();
        }
        if (this.inPVPMode) {
            this.player.setPlayerListName((Object)ChatColor.RED + "[PVP] " + name);
        } else {
            this.player.setPlayerListName((Object)ChatColor.GREEN + "[PVE] " + name);
        }
        this.player.setDisplayName(name);
    }
}

