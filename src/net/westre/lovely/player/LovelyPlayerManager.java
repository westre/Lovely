/*
 * Decompiled with CFR 0_101.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.westre.lovely.player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.UUID;
import net.westre.lovely.LovelyManager;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.entity.Player;

public class LovelyPlayerManager
extends LovelyManager<LovelyPlayer, UUID> {
    public LovelyPlayerManager() {
        System.out.println("LovelyPlayerManager created");
    }

    public LovelyPlayer getOnlinePlayer(Player player) {
        for (LovelyPlayer lovelyPlayer : super.getAll()) {
            if (lovelyPlayer.getPlayer() == null || !lovelyPlayer.getPlayer().equals((Object)player)) continue;
            return lovelyPlayer;
        }
        return null;
    }

    public String toString() {
        return super.getAll().toString();
    }
}

