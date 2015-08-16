package net.westre.lovely.listener;

import java.util.HashMap;
import java.util.UUID;
import net.westre.lovely.Main;
import net.westre.lovely.block.LovelyBlock;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.InventoryHolder;

public class BlockListener implements Listener {
    private HashMap<UUID, LovelyPlayer> temporaryBlockData = new HashMap();

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        boolean registerBlock = true;
        LovelyBlock placedBlocked = new LovelyBlock(event.getBlockPlaced(), Main.getPlayerManager().get(event.getPlayer().getUniqueId()));
        System.out.println(placedBlocked.getLinkedObject().getType().toString());

        if (placedBlocked.getLinkedObject().getType() == Material.WOODEN_DOOR || placedBlocked.getLinkedObject().getType() == Material.CHEST) {
            Chest chest;
            InventoryHolder inventoryHolder;
            placedBlocked.setLocked(true);

            if (event.getBlockPlaced().getState() instanceof Chest && (inventoryHolder = (chest = (Chest)event.getBlockPlaced().getState()).getInventory().getHolder()) instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest)inventoryHolder;
                registerBlock = false;
            }
        }
        if (registerBlock) {
            Main.getBlockManager().add(placedBlocked);
            Main.getBlockProcessingQueue().getInsertBlocksQueue().add(placedBlocked);
        }
    }

    @EventHandler
    public void onBlockDamaged(BlockDamageEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        LovelyBlock removedBlock = Main.getBlockManager().get(event.getBlock());
        if (removedBlock != null) {
            Main.getBlockManager().remove(event.getBlock());
            Main.getBlockProcessingQueue().getRemoveBlocksQueue().add(removedBlock);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {

    }

    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getBlock().getType() != Material.AIR) {
            LovelyBlock lovelyBlock = Main.getBlockManager().get(event.getBlock());

            if (Main.getBlockProcessingQueue().getInsertedBlock(lovelyBlock) != null) {
                Bukkit.broadcastMessage("Block is currently in processing queue, deleting it from said queue + manager");
                Main.getBlockProcessingQueue().getInsertBlocksQueue().remove(lovelyBlock);
                this.temporaryBlockData.put(event.getEntity().getUniqueId(), lovelyBlock.getOwner());
                Bukkit.broadcastMessage("Key: " + event.getEntity().getUniqueId() + ", owner: " + lovelyBlock.getOwner());
                Main.getBlockManager().remove(event.getBlock());
            } else {
                Bukkit.broadcastMessage("Block is not in processing queue, adding to removal queue + manager");
                Main.getBlockProcessingQueue().getRemoveBlocksQueue().add(lovelyBlock);
                Main.getBlockManager().remove(event.getBlock());
            }
        }
        else if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getBlock().getType() == Material.AIR) {
            LovelyPlayer lovelyPlayer = this.temporaryBlockData.get(event.getEntity().getUniqueId());
            Bukkit.broadcastMessage("Key: " + event.getEntity().getUniqueId() + ", owner: " + lovelyPlayer);
            LovelyBlock lovelyBlock = new LovelyBlock(event.getBlock(), lovelyPlayer);
            Main.getBlockManager().add(lovelyBlock);
            Main.getBlockProcessingQueue().getInsertBlocksQueue().add(lovelyBlock);
            this.temporaryBlockData.remove(event.getEntity().getUniqueId());
        }
    }
}

