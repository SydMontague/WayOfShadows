package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.craftlancer.wayofshadows.skills.PickPocket;

public class ShadowPickPocketFailEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Player victim;
    private FailReason reason;
    
    public ShadowPickPocketFailEvent(Player player, PickPocket skill, Player victim, FailReason reason)
    {
        super(player, skill);
        this.victim = victim;
        this.reason = reason;
    }
    
    @Override
    public PickPocket getSkill()
    {
        return (PickPocket) super.getSkill();
    }
    
    /**
     * Get the player who gets robbed
     * 
     * @return the robbed Player
     */
    public Player getVictim()
    {
        return victim;
    }
    
    /**
     * Get the reason, why the pickpocket has ended
     * 
     * @return the reason
     */
    public FailReason getReason()
    {
        return reason;
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
