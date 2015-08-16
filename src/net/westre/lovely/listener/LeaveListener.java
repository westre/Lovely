package net.westre.lovely.listener;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.westre.lovely.Main;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LovelyPlayer lovelyPlayer = Main.getPlayerManager().get(event.getPlayer().getUniqueId());

        if (lovelyPlayer.getPlayer() != null) {
            try {
                PreparedStatement preparedStatement = Main.getConnection().prepareStatement("UPDATE player SET minutes_played = ?, pvp_mode = ?");
                preparedStatement.setInt(1, lovelyPlayer.getMinutesPlayed());
                preparedStatement.setBoolean(2, lovelyPlayer.isInPVPMode());
                preparedStatement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("LovelyPlayer set to null");
            lovelyPlayer.setPlayer(null);
        }
    }
}

