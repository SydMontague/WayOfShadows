package de.craftlancer.wayofshadows.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import de.craftlancer.wayofshadows.skills.Skill;

public abstract class ShadowEvent extends Event implements Cancellable
{
    private Player player;
    private Skill skill;
    private boolean cancel = false;
    
    public ShadowEvent(Player player, Skill skill)
    {
        this.player = player;
        this.skill = skill;
    }
    
    /**
     * Get the player who performs this skill
     * 
     * @return the performing Player
     */
    public Player getPlayer()
    {
        return player;
    }
    
    /**
     * Get the skill, which is used for this event
     * 
     * @return the Skill used for this event
     */
    public Skill getSkill()
    {
        return skill;
    }
    
    /**
     * Gets the cancellation state of this event
     * 
     * @return true if cancelled, false if not
     */
    @Override
    public boolean isCancelled()
    {
        return cancel;
    }
    
    /**
     * Set if the event should be cancelled or not.
     * 
     * @param cancel - boolean if the event should be cancelled
     */
    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }
}
