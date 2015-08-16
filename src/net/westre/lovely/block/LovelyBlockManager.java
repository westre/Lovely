package net.westre.lovely.block;

import net.westre.lovely.LovelyManager;
import org.bukkit.block.Block;

public class LovelyBlockManager extends LovelyManager<LovelyBlock, Block> {

    public LovelyBlockManager() {
        System.out.println("LovelyBlockManager created");
    }

    public String toString() {
        return super.getAll().toString();
    }
}
