package de.craftlancer.wayofshadows;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

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
        
        int level = plugin.getSkillLevels() != null ? plugin.getSkillLevels().getUserLevel(getLevelSys(), p.getName()) : 0;
        
        if (p.getFallDistance() < minHeight.getValue(level))
            return;
        
        double height = ((p.getFallDistance() - minHeight.getValue(level)) < maxHeight.getValue(level) ? p.getFallDistance() - minHeight.getValue(level) : maxHeight.getValue(level));
        
        if (Math.random() <= chance.getValue(level))
        {
            e.setDamage(e.getDamage() + height * damagePerHeight.getValue(level));
            if (negateFallDamage)
                p.setFallDistance(0);
        }
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
}
