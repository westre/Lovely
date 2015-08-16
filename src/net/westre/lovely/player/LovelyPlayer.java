package net.westre.lovely.player;

import net.md_5.bungee.api.ChatColor;
import net.westre.lovely.LovelyLinkable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LovelyPlayer implements LovelyLinkable<UUID> {
    private OfflinePlayer offlinePlayer;
    private Player player;
    private int minutesPlayed;
    private boolean inPVPMode;

    public LovelyPlayer(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

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
        String name = player.getName();

        if(this.getMinutesPlayed() >= 60 && this.getMinutesPlayed() < 300) { // 1 to 5 hours
            name = ChatColor.GOLD + "" + ChatColor.BOLD + "+" + ChatColor.RESET + player.getName();
        }
        else if(this.getMinutesPlayed() >= 300 && this.getMinutesPlayed() < 1200) { // 5 to 20 hours
            name = ChatColor.GOLD + "" + ChatColor.BOLD + "++" + ChatColor.RESET + player.getName();
        }
        else if(this.getMinutesPlayed() >= 1200 && this.getMinutesPlayed() < 6000) { // 20 to 100 hours
            name = ChatColor.GOLD + "" + ChatColor.BOLD + "+++" + ChatColor.RESET + player.getName();
        }
        else if(this.getMinutesPlayed() >= 6000) { // 1 to 5 hours
            name = ChatColor.GOLD + "" + ChatColor.BOLD + "++++" + ChatColor.RESET + player.getName();
        }

        if(inPVPMode)
            player.setPlayerListName(ChatColor.RED + "[PVP] " + name);
        else
            player.setPlayerListName(ChatColor.GREEN + "[PVE] " + name);

        player.setDisplayName(name);
    }
}
