package net.westre.lovely.block;

import net.westre.lovely.LovelyLinkable;
import net.westre.lovely.player.LovelyPlayer;
import org.bukkit.block.Block;

public class LovelyBlock implements LovelyLinkable<Block> {

    private Block block;
    private LovelyPlayer owner;
    private Boolean locked;

    public LovelyBlock(Block block, LovelyPlayer owner) {
        this.block = block;
        this.owner = owner;
    }

    public Block getLinkedObject() {
        return block;
    }

    public LovelyPlayer getOwner() {
        return this.owner;
    }

    public Boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean toggle) {
        this.locked = toggle;
    }
}
