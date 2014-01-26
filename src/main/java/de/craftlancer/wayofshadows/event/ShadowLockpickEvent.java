package de.craftlancer.wayofshadows.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.Skill;

public class ShadowLockpickEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Block block;
    private boolean success;
    
    public ShadowLockpickEvent(Player player, Skill skill, Block block, boolean success)
    {
        super(player, skill);
        this.block = block;
        this.success = success;
    }
    
    public Block getBlock()
    {
        return block;
    }
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
