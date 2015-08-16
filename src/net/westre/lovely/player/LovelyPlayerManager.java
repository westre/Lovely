package net.westre.lovely.player;

import net.westre.lovely.LovelyManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LovelyPlayerManager extends LovelyManager<LovelyPlayer, UUID> {

    public LovelyPlayerManager() {
        System.out.println("LovelyPlayerManager created");
    }

    public LovelyPlayer getOnlinePlayer(Player player) {
        for(LovelyPlayer lovelyPlayer : super.getAll()) {
            if(lovelyPlayer.getPlayer() != null && lovelyPlayer.getPlayer().equals(player)) {
                return lovelyPlayer;
            }
        }
        return null;
    }

    public String toString() {
        return super.getAll().toString();
    }
}
