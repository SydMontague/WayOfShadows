package de.craftlancer.wayofshadows.skills;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO test
//TODO add events
public class Counter extends Skill
{
    private ValueWrapper maxTime;
    private ValueWrapper damage;
    private boolean multi; // true multiply, false add
    
    public Counter(WayOfShadows instance, String key)
    {
        super(instance, key);
        maxTime = new ValueWrapper(instance.getConfig().getString(getName() + ".maxTime", "10"));
        damage = new ValueWrapper(instance.getConfig().getString(getName() + ".damage", "2-y/10"));
        multi = instance.getConfig().getBoolean(getName() + ".multi", true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlock(final PlayerInteractEvent event)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                    return;
                
                Player player = event.getPlayer();
                ItemStack item = player.getItemInHand();
                
                if (!player.isBlocking())
                    return;
                
                if (!isSkillItem(item) || !hasPermission(player, item))
                    return;
                
                if (isOnCooldown(player))
                    return;
                
                player.removeMetadata("shadow." + getName() + ".counterTime", plugin);
                player.setMetadata("shadow." + getName() + ".blockTime", new FixedMetadataValue(plugin, event.getPlayer().getWorld().getFullTime()));
            }
        }.runTaskLater(plugin, 0);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
        {
            Player player = (Player) event.getEntity();
            if (player.hasMetadata("shadow." + getName() + ".blockTime") && player.isBlocking())
            {
                player.setMetadata("shadow." + getName() + ".counterTime", new FixedMetadataValue(plugin, player.getWorld().getFullTime()));
                player.sendMessage("Counter chance!");
            }
        }
        
        if (event.getDamager().getType() != EntityType.PLAYER)
            return;
        
        Player player = (Player) event.getDamager();
        
        if (!player.hasMetadata("shadow." + getName() + ".counterTime") || !player.hasMetadata("shadow." + getName() + ".blockTime"))
            return;
        
        long delta = player.getMetadata("shadow." + getName() + ".counterTime").get(0).asLong() - player.getMetadata("shadow." + getName() + ".blockTime").get(0).asLong();
        int level = plugin.getLevel(player, getLevelSys());
        
        if (delta > maxTime.getValue(level))
            return;
        
        double damageMod = damage.getValue(level, delta);
        
        if (multi)
            event.setDamage(event.getDamage(DamageModifier.BASE) * damageMod);
        else
            event.setDamage(event.getDamage(DamageModifier.BASE) + damageMod);
        
        player.sendMessage("Counter!");
        player.removeMetadata("shadow." + getName() + ".counterTime", plugin);
        player.removeMetadata("shadow." + getName() + ".blockTime", plugin);
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.COUNTER;
    }
}
