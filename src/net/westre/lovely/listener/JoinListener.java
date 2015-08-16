package net.westre.lovely.listener;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.westre.lovely.Main;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener
implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.initializePlayer(event.getPlayer());
    }

    public void initializePlayer(Player player) {
        System.out.println("SIZE: " + Main.getPlayerManager().getAll().size());
        LovelyPlayer lovelyPlayer = Main.getPlayerManager().get(player.getUniqueId());

        if (lovelyPlayer == null) {
            player.sendMessage("Welcome new player, a new account will be created for you!");
            try {
                PreparedStatement preparedStatement = Main.getConnection().prepareStatement("INSERT INTO player (id, name) VALUES (?, ?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getName());
                preparedStatement.execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            lovelyPlayer = new LovelyPlayer(Bukkit.getOfflinePlayer((UUID)player.getUniqueId()));
            lovelyPlayer.setMinutesPlayed(0);
            lovelyPlayer.setInPVPMode(false);
            Main.getPlayerManager().add(lovelyPlayer);
        }
        else {
            player.sendMessage("Welcome back. :)");
        }

        lovelyPlayer.setPlayer(player);
        lovelyPlayer.updateDisplay();
        Bukkit.broadcastMessage((String) "Welcome to the server!");
    }
}

