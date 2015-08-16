package net.westre.lovely;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.westre.lovely.block.LovelyBlock;
import org.bukkit.Bukkit;

public class BlockProcessingQueue {
    private Main main;
    private ArrayList<LovelyBlock> insertBlocks = new ArrayList();
    private ArrayList<LovelyBlock> removeBlocks = new ArrayList();
    private ArrayList<LovelyBlock> updateBlocks = new ArrayList();

    public BlockProcessingQueue(Main main) {
        this.main = main;
    }

    public ArrayList<LovelyBlock> getInsertBlocksQueue() {
        return this.insertBlocks;
    }

    public ArrayList<LovelyBlock> getUpdateBlocksQueue() {
        return this.updateBlocks;
    }

    public LovelyBlock getInsertedBlock(LovelyBlock lovelyBlock) {
        for (LovelyBlock loopBlock : this.insertBlocks) {
            if (!lovelyBlock.equals(loopBlock)) continue;
            return loopBlock;
        }
        return null;
    }

    public ArrayList<LovelyBlock> getRemoveBlocksQueue() {
        return this.removeBlocks;
    }

    public void process() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.main, new Runnable() {

            @Override
            public void run() {
                ArrayList<LovelyBlock> blocksInsertProcess = new ArrayList<LovelyBlock>(BlockProcessingQueue.this.insertBlocks);
                ArrayList<LovelyBlock> blocksRemoveProcess = new ArrayList<LovelyBlock>(BlockProcessingQueue.this.removeBlocks);
                ArrayList<LovelyBlock> blocksUpdateProcess = new ArrayList<LovelyBlock>(BlockProcessingQueue.this.updateBlocks);
                
                try {
                    PreparedStatement preparedStatement;
                    Main.getConnection().setAutoCommit(false);
                    String debugString = ChatColor.GRAY + "Processed ";
                    int processed = 0;
                    for (LovelyBlock blockAction : blocksInsertProcess) {
                        if (blockAction.isLocked() != null) {
                            preparedStatement = Main.getConnection().prepareStatement("INSERT INTO block (user_id, x, y, z, `type`, world, locked) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            preparedStatement.setBoolean(7, blockAction.isLocked());
                        } else {
                            preparedStatement = Main.getConnection().prepareStatement("INSERT INTO block (user_id, x, y, z, `type`, world) VALUES (?, ?, ?, ?, ?, ?)");
                        }
                        preparedStatement.setString(1, blockAction.getOwner().getLinkedObject().toString());
                        preparedStatement.setInt(2, blockAction.getLinkedObject().getX());
                        preparedStatement.setInt(3, blockAction.getLinkedObject().getY());
                        preparedStatement.setInt(4, blockAction.getLinkedObject().getZ());
                        preparedStatement.setString(5, blockAction.getLinkedObject().getType().name());
                        preparedStatement.setString(6, blockAction.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        ++processed;
                    }
                    debugString = debugString + processed + " inserts, ";
                    processed = 0;
                    
                    for (LovelyBlock blockAction : blocksUpdateProcess) {
                        preparedStatement = Main.getConnection().prepareStatement("UPDATE block SET locked = ? WHERE x = ? AND y = ? AND z = ? AND world = ?");
                        preparedStatement.setBoolean(1, blockAction.isLocked());
                        preparedStatement.setInt(2, blockAction.getLinkedObject().getX());
                        preparedStatement.setInt(3, blockAction.getLinkedObject().getY());
                        preparedStatement.setInt(4, blockAction.getLinkedObject().getZ());
                        preparedStatement.setString(5, blockAction.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        ++processed;
                    }
                    debugString = debugString + processed + " updates, ";
                    processed = 0;
                    
                    for (LovelyBlock blockAction : blocksRemoveProcess) {
                        preparedStatement = Main.getConnection().prepareStatement("DELETE FROM block WHERE x = ? AND y = ? AND z = ? AND world = ?");
                        preparedStatement.setInt(1, blockAction.getLinkedObject().getX());
                        preparedStatement.setInt(2, blockAction.getLinkedObject().getY());
                        preparedStatement.setInt(3, blockAction.getLinkedObject().getZ());
                        preparedStatement.setString(4, blockAction.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        ++processed;
                    }
                    
                    debugString = debugString + processed + " removals.";
                    BlockProcessingQueue.this.insertBlocks.removeAll(blocksInsertProcess);
                    BlockProcessingQueue.this.updateBlocks.removeAll(blocksUpdateProcess);
                    BlockProcessingQueue.this.removeBlocks.removeAll(blocksRemoveProcess);
                    Main.getConnection().commit();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 5, 150);
    }

}

