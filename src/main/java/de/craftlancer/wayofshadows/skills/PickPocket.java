package de.craftlancer.wayofshadows.skills;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.PickPocketCheckTask;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO add damage value support
//TODO steal money?
/**
 * Represents a configuration of the PickPocket skill
 */
public class PickPocket extends ThiefSkill
{
    private ValueWrapper maxAngle;
    private ValueWrapper maxDistance;
    
    public PickPocket(WayOfShadows plugin, String key)
    {
        super(plugin, key);
        FileConfiguration config = plugin.getConfig();
        
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        maxDistance = new ValueWrapper(config.getString(key + ".maxDistance", "10"));
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        
        if (!e.getRightClicked().getType().equals(EntityType.PLAYER))
            return;
        
        Player victim = (Player) e.getRightClicked();
        
        if(victim.getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        
        if (!isSkillItem(item) || !hasPermission(p, item, victim))
            return;
        
        double angle = Utils.getAngle(p.getLocation().getDirection(), victim.getLocation().getDirection());
        int level = plugin.getLevel(p, getLevelSys());
        
        if ((!getOnSneak() || p.isSneaking()) && angle < maxAngle.getValue(level))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            p.openInventory(victim.getInventory());
            p.setMetadata("stealingPlayer." + getName(), new FixedMetadataValue(plugin, victim.getInventory()));
            p.setMetadata("stealingValue." + getName(), new FixedMetadataValue(plugin, 0));
            
            new PickPocketCheckTask(p, victim, this, level).runTaskTimer(plugin, 10L, 10L);
        }
    }
    
    public ValueWrapper getMaxDistance()
    {
        return maxDistance;
    }
    
    public ValueWrapper getMaxAngle()
    {
        return maxAngle;
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".maxAngle", maxAngle.getInput());
        config.set(getName() + ".onSneak", getOnSneak());
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.PICKPOCKET;
    }
}
