package de.craftlancer.wayofshadows.skills;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.event.ShadowBackStabEvent;
import de.craftlancer.wayofshadows.updater.BackstabItem;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

/**
 * Represents a configuration of the BackStab skill
 */
public class BackStab extends Skill
{
    private ValueWrapper chance;
    private ValueWrapper critChance;
    private ValueWrapper multiplier;
    private ValueWrapper critMultiplier;
    private ValueWrapper maxAngle;
    private boolean onSneak;
    private boolean critOnSneak;
    
    private String attackerMsg;
    private String victimMsg;
    private String critMsg;
    
    public BackStab(WayOfShadows instance, String key)
    {
        super(instance, key);
        
        FileConfiguration config = instance.getConfig();
        
        chance = new ValueWrapper(config.getString(key + ".chance", "0"));
        critChance = new ValueWrapper(config.getString(key + ".critChance", "0"));
        multiplier = new ValueWrapper(config.getString(key + ".multiplier", "1"));
        critMultiplier = new ValueWrapper(config.getString(key + ".critMultiplier", "1"));
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        
        onSneak = config.getBoolean(key + ".onSneak", false);
        critOnSneak = config.getBoolean(key + ".critOnSneak", false);
        
        attackerMsg = config.getString(key + ".attackerMsg");
        victimMsg = config.getString(key + ".victimMsg");
        critMsg = config.getString(key + ".critMsg");
    }
    
    /**
     * Constructor for pre 0.5 Updater
     * @param instance plugin instance
     * @param key of the skill
     * @param item Material/ItemID of the skill item
     * @param value BackstabValue
     * @param msg1 attackerMsg
     * @param msg2 victimMsg
     * @param msg3 critMsg
     */
    @Deprecated
    public BackStab(WayOfShadows instance, String key, String item, BackstabItem value, String msg1, String msg2, String msg3)
    {
        super(instance, key, item);
        chance = new ValueWrapper(value.chance);
        critChance = new ValueWrapper(value.critchance);
        multiplier = new ValueWrapper(value.multiplier);
        critMultiplier = new ValueWrapper(value.critmultiplier);
        maxAngle = new ValueWrapper(value.angle);
        
        onSneak = value.sneak;
        critOnSneak = value.critsneak;
        
        attackerMsg = msg1;
        victimMsg = msg2;
        critMsg = msg3;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent e)
    {
        ItemStack item;
        Player p;
        
        if(e.getDamager().equals(e.getEntity()))
            return;
        
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
        
        if (p == null || !isSkillItem(item) || !hasPermission(p, item))
            return;
        
        if (!(e.getEntity() instanceof LivingEntity) || ((LivingEntity) e.getEntity()).getNoDamageTicks() >= 1)
            return;
        
        double angle = Utils.getAngle(e.getEntity().getLocation().getDirection(), p.getLocation().getDirection());
        int level = plugin.getLevel(p, getLevelSys());
        
        if (angle < maxAngle.getValue(level) && (!onSneak || p.isSneaking()) && Math.random() <= chance.getValue(level))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            ShadowBackStabEvent event = new ShadowBackStabEvent(p, this, e.getEntity());
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            if (event.isCancelled())
                return;
            
            if (Math.random() <= critChance.getValue(level) && (!critOnSneak || p.isSneaking()))
            {
                e.setDamage(e.getDamage() * critMultiplier.getValue(level));
                p.sendMessage(critMsg);
            }
            else
            {
                e.setDamage(e.getDamage() * multiplier.getValue(level));
                p.sendMessage(attackerMsg);
            }
            if (e.getEntity().getType().equals(EntityType.PLAYER))
                ((Player) e.getEntity()).sendMessage(victimMsg);
            
            setOnCooldown(p);
        }
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        config.set(getName() + ".chance", chance.getInput());
        config.set(getName() + ".critChance", critChance.getInput());
        config.set(getName() + ".multiplier", multiplier.getInput());
        config.set(getName() + ".critMultiplier", critMultiplier.getInput());
        config.set(getName() + ".maxAngle", maxAngle.getInput());
        
        config.set(getName() + ".onSneak", onSneak);
        config.set(getName() + ".critOnSneak", critOnSneak);
        
        config.set(getName() + ".attackerMsg", attackerMsg);
        config.set(getName() + ".victimMsg", victimMsg);
        config.set(getName() + ".critMsg", critMsg);
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.BACKSTAB;
    }
}
