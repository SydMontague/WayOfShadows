package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

import de.craftlancer.wayofshadows.skills.Skill;
import de.craftlancer.wayofshadows.skills.ThiefSkill;

/**
 * Gets called when a pickpocket comes to it's end,
 */
public class ShadowPickPocketEndEvent extends ShadowEvent
{
    private static final HandlerList handlers = new HandlerList();
    private Inventory victim;
    private EndReason reason;
    
    public ShadowPickPocketEndEvent(Player player, Skill skill, Inventory victim, EndReason reason)
    {
        super(player, skill);
        this.victim = victim;
        this.reason = reason;
    }
    
    @Override
    public ThiefSkill getSkill()
    {
        return (ThiefSkill) super.getSkill();
    }
    
    /**
     * Get the player who gets robbed
     * 
     * @return the robbed Player
     */
    public Inventory getVictim()
    {
        return victim;
    }
    
    /**
     * Get the reason, why the pickpocket has ended
     * 
     * @return the reason
     */
    public EndReason getReason()
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
