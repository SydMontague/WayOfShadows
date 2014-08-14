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

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;


//TODO test
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
    public void onBlock(PlayerInteractEvent event)
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
        
        player.setMetadata("shadow." + getName() + ".blockTime", new FixedMetadataValue(plugin, event.getPlayer().getWorld().getFullTime()));
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
        {
            Player player = (Player) event.getEntity();
            if (player.hasMetadata("shadow." + getName() + ".blockTime") && player.isBlocking())
                player.setMetadata("shadow." + getName() + ".counterTime", new FixedMetadataValue(plugin, player.getWorld().getFullTime()));
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
            event.setDamage(DamageModifier.BASE, event.getDamage(DamageModifier.BASE) * damageMod);
        else
            event.setDamage(DamageModifier.BASE, event.getDamage(DamageModifier.BASE) + damageMod);
        
        player.removeMetadata("shadow." + getName() + ".counterTime", plugin);
        player.removeMetadata("shadow." + getName() + ".blockTime", plugin);
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.COUNTER;
    }
}
