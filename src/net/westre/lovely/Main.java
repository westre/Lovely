package net.westre.lovely;

import net.minecraft.server.v1_8_R3.World;
import net.westre.lovely.block.LovelyBlock;
import net.westre.lovely.block.LovelyBlockManager;
import net.westre.lovely.listener.BlockListener;
import net.westre.lovely.listener.JoinListener;
import net.westre.lovely.listener.LeaveListener;
import net.westre.lovely.listener.PlayerListener;
import net.westre.lovely.player.LovelyPlayer;
import net.westre.lovely.player.LovelyPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

public class Main extends JavaPlugin {

    private JoinListener joinListener;
    private LeaveListener leaveListener;
    private BlockListener blockListener;
    private PlayerListener playerListener;

    private static LovelyPlayerManager lovelyPlayerManager;
    private static LovelyBlockManager lovelyBlockManager;
    private static BlockProcessingQueue blockProcessingQueue;

    private static Connection connection;

    @Override
    public void onEnable() {
        joinListener = new JoinListener();
        leaveListener = new LeaveListener();
        blockListener = new BlockListener();
        playerListener = new PlayerListener();

        getServer().getPluginManager().registerEvents(joinListener, this);
        getServer().getPluginManager().registerEvents(leaveListener, this);
        getServer().getPluginManager().registerEvents(blockListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        lovelyPlayerManager = new LovelyPlayerManager();
        lovelyBlockManager = new LovelyBlockManager();

        for(LovelyPlayer lovelyPlayer : lovelyPlayerManager.getAll()) {
            lovelyPlayerManager.remove(lovelyPlayer.getLinkedObject());
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://westre.net:3306/lovely", "lovely", "lovely1337");

            // fill up the initial arraylist for players
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("id"));
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerUUID);

                if(lovelyPlayerManager.get(playerUUID) == null) {
                    LovelyPlayer lovelyPlayer = new LovelyPlayer(offlinePlayer);
                    lovelyPlayer.setMinutesPlayed(resultSet.getInt("minutes_played"));
                    lovelyPlayer.setInPVPMode(resultSet.getBoolean("pvp_mode"));

                    lovelyPlayerManager.add(lovelyPlayer);
                }
                else
                    System.out.println("Player already in server, this should never happen");
            }

            // fill up the initial arraylist for blocks
            preparedStatement = connection.prepareStatement("SELECT * FROM block");
            resultSet = preparedStatement.executeQuery();

            int outOfSyncBlockCount = 0;
            while(resultSet.next()) {
                Block block = getServer().getWorld("world").getBlockAt(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                LovelyPlayer lovelyPlayer = lovelyPlayerManager.get(UUID.fromString(resultSet.getString("user_id")));

                if(block != null) {
                    LovelyBlock lovelyBlock = new LovelyBlock(block, lovelyPlayer);

                    boolean isLocked = resultSet.getBoolean("locked");
                    if(!resultSet.wasNull()) {
                        lovelyBlock.setLocked(isLocked);
                    }

                    lovelyBlockManager.add(lovelyBlock);
                }
                else {
                    outOfSyncBlockCount++;
                }
            }

            System.out.println(outOfSyncBlockCount + " blocks out of sync");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            joinListener.initializePlayer(player);
        }

        blockProcessingQueue = new BlockProcessingQueue(this);
        blockProcessingQueue.process();

        minuteTimer();
    }

    @Override
    public void onDisable() {
        try {
            if(connection != null && connection.isClosed()) {
                connection.close();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static LovelyPlayerManager getPlayerManager() {
        return lovelyPlayerManager;
    }

    public static LovelyBlockManager getBlockManager() {
        return lovelyBlockManager;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static BlockProcessingQueue getBlockProcessingQueue() {
        return blockProcessingQueue;
    }

    private void minuteTimer() {
        Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                for(LovelyPlayer lovelyPlayer : Main.getPlayerManager().getAll()) {
                    if(lovelyPlayer.getPlayer() != null) {
                        lovelyPlayer.setMinutesPlayed(lovelyPlayer.getMinutesPlayed() + 1);
                        lovelyPlayer.updateDisplay();
                    }
                }
            }
        }, 200, 200); // 20 * seconds = output
    }
}
