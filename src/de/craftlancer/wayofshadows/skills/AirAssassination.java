package de.craftlancer.wayofshadows.skills;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.event.ShadowAirAssassinEvent;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

/**
 * Represents a configuration of the AirAssasionation skill
 */
public class AirAssassination extends Skill
{
    private ValueWrapper chance;
    private ValueWrapper minHeight;
    private ValueWrapper maxHeight;
    private ValueWrapper damagePerHeight;
    private boolean negateFallDamage;
    
    public AirAssassination(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        chance = new ValueWrapper(config.getString(key + ".chance", "0"));
        minHeight = new ValueWrapper(config.getString(key + ".minHeight", "0"));
        maxHeight = new ValueWrapper(config.getString(key + ".maxHeight", "0"));
        damagePerHeight = new ValueWrapper(config.getString(key + ".damagePerHeight", "0"));
        negateFallDamage = config.getBoolean(key + ".negateFallOnSuccess", false);
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e)
    {
        ItemStack item = null;
        Player p = null;
        
        if (e.getDamager().getType().equals(EntityType.PLAYER))
        {
            p = (Player) e.getDamager();
            item = p.getItemInHand();
        }
        else if (e.getDamager() instanceof Projectile && e.getDamager().hasMetadata("shootingItem"))
        {
            p = (Player) ((Projectile) e.getDamager()).getShooter();
            item = (ItemStack) e.getDamager().getMetadata("shootingItem").get(0).value();
        }
        else
            return;
        
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        int level = plugin.getLevel(p, getLevelSys());
        
        if (p.getFallDistance() < minHeight.getValue(level))
            return;
        
        if (isOnCooldown(p))
        {
            p.sendMessage(getCooldownMsg(p));
            return;
        }
        
        double height = ((p.getFallDistance() - minHeight.getValue(level)) < maxHeight.getValue(level) ? p.getFallDistance() - minHeight.getValue(level) : maxHeight.getValue(level));
        
        if (Math.random() <= chance.getValue(level))
        {
            ShadowAirAssassinEvent event = new ShadowAirAssassinEvent(p, this, e.getEntity(), height);
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            if (event.isCancelled())
                return;
            
            e.setDamage(e.getDamage() + height * damagePerHeight.getValue(level));
            if (negateFallDamage)
                p.setFallDistance(0);
        }
        
        setOnCooldown(p);
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".type", "airassassination");
        config.set(getName() + ".chance", chance.getInput());
        config.set(getName() + ".minHeight", minHeight.getInput());
        config.set(getName() + ".maxHeight", maxHeight.getInput());
        config.set(getName() + ".damagePerHeight", damagePerHeight.getInput());
        config.set(getName() + ".negateFallDamage", negateFallDamage);
    }
    
    @Override
    public String getType()
    {
        return "airassassination";
    }
}
