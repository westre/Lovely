package net.westre.lovely;

import net.md_5.bungee.api.ChatColor;
import net.westre.lovely.block.LovelyBlock;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlockProcessingQueue {

    private Main main;
    private ArrayList<LovelyBlock> insertBlocks;
    private ArrayList<LovelyBlock> removeBlocks;
    private ArrayList<LovelyBlock> updateBlocks;

    public BlockProcessingQueue(Main main) {
        this.insertBlocks = new ArrayList<LovelyBlock>();
        this.removeBlocks = new ArrayList<LovelyBlock>();
        this.updateBlocks = new ArrayList<LovelyBlock>();

        this.main = main;
    }

    public ArrayList<LovelyBlock> getInsertBlocksQueue() {
        return this.insertBlocks;
    }

    public ArrayList<LovelyBlock> getUpdateBlocksQueue() {
        return this.updateBlocks;
    }

    public LovelyBlock getInsertedBlock(LovelyBlock lovelyBlock) {
        for(LovelyBlock loopBlock : this.insertBlocks) {
            if(lovelyBlock.equals(loopBlock))
                return loopBlock;
        }
        return null;
    }

    public ArrayList<LovelyBlock> getRemoveBlocksQueue() {
        return this.removeBlocks;
    }

    public void process() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(main, new Runnable() {
            public void run() {
                ArrayList<LovelyBlock> blocksInsertProcess = new ArrayList<LovelyBlock>(insertBlocks);
                ArrayList<LovelyBlock> blocksRemoveProcess = new ArrayList<LovelyBlock>(removeBlocks);
                ArrayList<LovelyBlock> blocksUpdateProcess = new ArrayList<LovelyBlock>(updateBlocks);

                try {
                    Main.getConnection().setAutoCommit(false);

                    String debugString = ChatColor.GRAY + "Processed ";

                    int processed = 0;
                    for(LovelyBlock block : blocksInsertProcess) {
                        PreparedStatement preparedStatement;

                        if(block.isLocked() != null) { // chest, door, etc.
                            preparedStatement = Main.getConnection().prepareStatement("INSERT INTO block (user_id, x, y, z, `type`, world, locked) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            preparedStatement.setBoolean(7, block.isLocked());
                        }
                        else {
                            preparedStatement = Main.getConnection().prepareStatement("INSERT INTO block (user_id, x, y, z, `type`, world) VALUES (?, ?, ?, ?, ?, ?)");
                        }

                        preparedStatement.setString(1, block.getOwner().getLinkedObject().toString());
                        preparedStatement.setInt(2, block.getLinkedObject().getX());
                        preparedStatement.setInt(3, block.getLinkedObject().getY());
                        preparedStatement.setInt(4, block.getLinkedObject().getZ());
                        preparedStatement.setString(5, block.getLinkedObject().getType().name());
                        preparedStatement.setString(6, block.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        processed++;
                    }
                    debugString += processed + " inserts, ";

                    //System.out.println("Processed " + processed + " insert queries");

                    processed = 0;
                    for(LovelyBlock block : blocksUpdateProcess) {
                        PreparedStatement preparedStatement = Main.getConnection().prepareStatement("UPDATE block SET locked = ? WHERE x = ? AND y = ? AND z = ? AND world = ?");
                        preparedStatement.setBoolean(1, block.isLocked());
                        preparedStatement.setInt(2, block.getLinkedObject().getX());
                        preparedStatement.setInt(3, block.getLinkedObject().getY());
                        preparedStatement.setInt(4, block.getLinkedObject().getZ());
                        preparedStatement.setString(5, block.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        processed++;
                    }
                    debugString += processed + " updates, ";

                    processed = 0;
                    for(LovelyBlock block : blocksRemoveProcess) {
                        PreparedStatement preparedStatement = Main.getConnection().prepareStatement("DELETE FROM block WHERE x = ? AND y = ? AND z = ? AND world = ?");
                        preparedStatement.setInt(1, block.getLinkedObject().getX());
                        preparedStatement.setInt(2, block.getLinkedObject().getY());
                        preparedStatement.setInt(3, block.getLinkedObject().getZ());
                        preparedStatement.setString(4, block.getLinkedObject().getWorld().getName());
                        preparedStatement.execute();
                        processed++;
                    }
                    debugString += processed + " removals.";

                    //System.out.println("Processed " + processed + " delete queries");

                    insertBlocks.removeAll(blocksInsertProcess);
                    updateBlocks.removeAll(blocksUpdateProcess);
                    removeBlocks.removeAll(blocksRemoveProcess);

                    //System.out.println("Removed useless shit");
                    //Bukkit.broadcastMessage(debugString);

                    Main.getConnection().commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 100 / 20, 3000 / 20);
    }
}
