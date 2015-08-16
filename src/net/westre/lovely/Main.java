package net.westre.lovely;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
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

public class Main extends JavaPlugin {

    private JoinListener joinListener;
    private LeaveListener leaveListener;
    private BlockListener blockListener;
    private PlayerListener playerListener;
    private static LovelyPlayerManager lovelyPlayerManager;
    private static LovelyBlockManager lovelyBlockManager;
    private static BlockProcessingQueue blockProcessingQueue;
    private static Connection connection;

    public void onEnable() {
        this.joinListener = new JoinListener();
        this.leaveListener = new LeaveListener();
        this.blockListener = new BlockListener();
        this.playerListener = new PlayerListener();
        this.getServer().getPluginManager().registerEvents(this.joinListener, this);
        this.getServer().getPluginManager().registerEvents(this.leaveListener, this);
        this.getServer().getPluginManager().registerEvents(this.blockListener, this);
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);

        lovelyPlayerManager = new LovelyPlayerManager();
        lovelyBlockManager = new LovelyBlockManager();

        for (LovelyPlayer lovelyPlayer : lovelyPlayerManager.getAll()) {
            lovelyPlayerManager.remove(lovelyPlayer.getLinkedObject());
        }

        try {
            LovelyPlayer lovelyPlayer2;
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://westre.net:3306/lovely", "lovely", "lovelypw");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("id"));
                OfflinePlayer offlinePlayer = this.getServer().getOfflinePlayer(playerUUID);
                if (lovelyPlayerManager.get(playerUUID) == null) {
                    lovelyPlayer2 = new LovelyPlayer(offlinePlayer);
                    lovelyPlayer2.setMinutesPlayed(resultSet.getInt("minutes_played"));
                    lovelyPlayer2.setInPVPMode(resultSet.getBoolean("pvp_mode"));
                    lovelyPlayerManager.add(lovelyPlayer2);
                    continue;
                }
                System.out.println("Player already in server, this should never happen");
            }

            preparedStatement = connection.prepareStatement("SELECT * FROM block");
            resultSet = preparedStatement.executeQuery();
            int outOfSyncBlockCount = 0;

            while (resultSet.next()) {
                Block block = this.getServer().getWorld("world").getBlockAt(resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                lovelyPlayer2 = lovelyPlayerManager.get(UUID.fromString(resultSet.getString("user_id")));
                if (block != null) {
                    LovelyBlock lovelyBlock = new LovelyBlock(block, lovelyPlayer2);
                    boolean isLocked = resultSet.getBoolean("locked");
                    if (!resultSet.wasNull()) {
                        lovelyBlock.setLocked(isLocked);
                    }
                    lovelyBlockManager.add(lovelyBlock);
                    continue;
                }
                ++outOfSyncBlockCount;
            }

            System.out.println("" + outOfSyncBlockCount + " blocks out of sync");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.joinListener.initializePlayer(player);
        }

        blockProcessingQueue = new BlockProcessingQueue(this);
        blockProcessingQueue.process();

        this.minuteTimer();
    }

    public void onDisable() {
        try {
            if (connection != null && connection.isClosed()) {
                connection.close();
            }
        }
        catch (Exception e) {
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
        Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable(){

            @Override
            public void run() {
                for (LovelyPlayer lovelyPlayer : Main.getPlayerManager().getAll()) {
                    if (lovelyPlayer.getPlayer() == null) continue;
                    lovelyPlayer.setMinutesPlayed(lovelyPlayer.getMinutesPlayed() + 1);
                    lovelyPlayer.updateDisplay();
                }
            }
        }, 200, 200);
    }

}

