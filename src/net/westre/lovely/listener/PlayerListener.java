package net.westre.lovely.listener;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.westre.lovely.Main;
import net.westre.lovely.block.LovelyBlock;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.STICK) {
            if (event.getClickedBlock().getType() != Material.AIR && Main.getBlockManager().get(event.getClickedBlock()) != null) {
                LovelyBlock lovelyBlock = Main.getBlockManager().get(event.getClickedBlock());
                LovelyPlayer lovelyPlayer = lovelyBlock.getOwner();
                event.getPlayer().sendMessage(ChatColor.AQUA + "--------------------------------");
                event.getPlayer().sendMessage("This " + ChatColor.GOLD + ChatColor.BOLD + event.getClickedBlock().getType().name().toLowerCase() + ChatColor.RESET + " is owned by " + Bukkit.getOfflinePlayer(lovelyPlayer.getLinkedObject()).getName());
                if (lovelyBlock.isLocked() != null) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "- This entity is " + (lovelyBlock.isLocked() != false ? "locked" : "unlocked"));
                }
                if (Bukkit.getOfflinePlayer(lovelyPlayer.getLinkedObject()).isOnline()) {
                    event.getPlayer().sendMessage("This player is currently " + ChatColor.GREEN + "online");
                } else {
                    event.getPlayer().sendMessage("This player is currently " + ChatColor.RED + "offline");
                }
                event.getPlayer().sendMessage(ChatColor.AQUA + "--------------------------------");
            }
        }
        else if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getPlayer().getItemInHand().getType() != Material.STICK) {
            LovelyBlock lovelyBlock = Main.getBlockManager().get(event.getClickedBlock());

            if (event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
                if (Main.getBlockManager().get(event.getClickedBlock()) != null) {
                    if (lovelyBlock.isLocked() != null) {
                        lovelyBlock.setLocked(lovelyBlock.isLocked() == false);
                        Main.getBlockProcessingQueue().getUpdateBlocksQueue().add(lovelyBlock);
                        event.getPlayer().sendMessage("Door set to " + (lovelyBlock.isLocked() != false ? "locked" : "unlocked"));
                    }
                }
                else if (Main.getBlockManager().get(event.getClickedBlock().getLocation().add(0.0, -1.0, 0.0).getBlock()) != null) {
                    lovelyBlock.setLocked(!lovelyBlock.isLocked());
                    Main.getBlockProcessingQueue().getUpdateBlocksQueue().add(lovelyBlock);
                    event.getPlayer().sendMessage("Door set to " + (lovelyBlock.isLocked() != false ? "locked" : "unlocked"));
                }
            }
            else if (event.getClickedBlock().getType() == Material.CHEST && event.getClickedBlock().getState() instanceof Chest) {
                Chest chest = (Chest)event.getClickedBlock().getState();
                InventoryHolder inventoryHolder = chest.getInventory().getHolder();

                if (inventoryHolder instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest)inventoryHolder;
                    Chest leftChest = (Chest)doubleChest.getLeftSide();
                    Chest rightChest = (Chest)doubleChest.getRightSide();

                    Block[] blockLoop = new Block[]{
                            leftChest.getBlock(),
                            rightChest.getBlock()
                    };

                    for (int index = 0; index < blockLoop.length; ++index) {
                        LovelyPlayer playerClicked;

                        if (Main.getBlockManager().get(blockLoop[index]) == null) continue;

                        LovelyBlock chestBlock = Main.getBlockManager().get(blockLoop[index]);
                        if (chestBlock == null || (playerClicked = Main.getPlayerManager().getOnlinePlayer(event.getPlayer())) == null || !playerClicked.equals(chestBlock.getOwner())) break;
                        chestBlock.setLocked(chestBlock.isLocked() == false);
                        Main.getBlockProcessingQueue().getUpdateBlocksQueue().add(chestBlock);
                        event.getPlayer().sendMessage("DoubleChest set to " + (chestBlock.isLocked() != false ? "locked" : "unlocked"));
                        break;
                    }
                }
                else {
                    LovelyPlayer playerClicked = Main.getPlayerManager().getOnlinePlayer(event.getPlayer());
                    LovelyBlock chestBlock = Main.getBlockManager().get(chest.getBlock());

                    if (playerClicked != null && playerClicked.equals(chestBlock.getOwner())) {
                        chestBlock.setLocked(chestBlock.isLocked() == false);
                        Main.getBlockProcessingQueue().getUpdateBlocksQueue().add(chestBlock);
                        event.getPlayer().sendMessage("Chest set to " + (chestBlock.isLocked() != false ? "locked" : "unlocked"));
                    }
                }
            }
        }
    }
}

