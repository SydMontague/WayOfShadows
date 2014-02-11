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
    
    /**
     * Get the block that the player tries to pick.
     * 
     * @return the picked block
     */
    public Block getBlock()
    {
        return block;
    }
    
    /**
     * Get whether the picking is successful.
     * When the event is called, the plugin already calculated it's values.
     * 
     * @return whether the lock is picked or not
     */
    public boolean isSuccess()
    {
        return success;
    }
    
    /**
     * Set if the lockpick shall be a success or not
     * 
     * @param success true if the pick shall be broken, false if not
     */
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
