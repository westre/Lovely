package net.westre.lovely.listener;

import net.westre.lovely.Main;
import net.westre.lovely.Title;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        initializePlayer(event.getPlayer());
    }

    public void initializePlayer(Player player) {
        System.out.println("SIZE: " + Main.getPlayerManager().getAll().size());
        LovelyPlayer lovelyPlayer = Main.getPlayerManager().get(player.getUniqueId());

        if(lovelyPlayer == null) {
            player.sendMessage("Welcome new player, a new account will be created for you!");

            try {
                PreparedStatement preparedStatement = Main.getConnection().prepareStatement("INSERT INTO player (id, name) VALUES (?, ?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getName());
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            lovelyPlayer = new LovelyPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            lovelyPlayer.setMinutesPlayed(0);
            lovelyPlayer.setInPVPMode(false);

            Main.getPlayerManager().add(lovelyPlayer);
        }
        else {
            player.sendMessage("Welcome back. :)");
        }

        lovelyPlayer.setPlayer(player);
        lovelyPlayer.updateDisplay();

        Bukkit.broadcastMessage("Welcome to the server!");

        Title title = new Title("Welcome!", "", 2, 5, 2);
        title.setSubtitle("Hello :)");
        title.setTitleColor(ChatColor.RED);

        title.send(player);
    }
}
