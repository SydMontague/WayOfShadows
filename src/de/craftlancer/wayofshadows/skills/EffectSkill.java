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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.event.ShadowEffectSkillEvent;
import de.craftlancer.wayofshadows.updater.ItemEffect;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

public class EffectSkill extends Skill
{
    private PotionEffectType type;
    private ValueWrapper chance;
    private ValueWrapper maxAngle;
    private ValueWrapper duration;
    private ValueWrapper strength;
    private boolean onSneak;
    
    public EffectSkill(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        type = PotionEffectType.getById(config.getInt(key + ".effectType")) != null ? PotionEffectType.getById(config.getInt(key + ".effectType")) : PotionEffectType.getByName(config.getString(key + ".effectType"));
        
        chance = new ValueWrapper(config.getString(key + ".chance", "0"));
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        
        duration = new ValueWrapper(config.getString(key + ".duration", "0"));
        strength = new ValueWrapper(config.getString(key + ".strength", "0"));
        
        onSneak = config.getBoolean(key + ".onSneak", false);
    }
    
    public EffectSkill(WayOfShadows instance, String key, String item, ItemEffect i)
    {
        super(instance, key, item);
        
        type = i.type;
        chance = new ValueWrapper(i.chance);
        maxAngle = new ValueWrapper(i.angle);
        duration = new ValueWrapper(i.duration);
        strength = new ValueWrapper(i.strength);
        onSneak = i.sneak;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent e)
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
        if (!(e.getEntity() instanceof LivingEntity) || ((LivingEntity) e.getEntity()).getNoDamageTicks() >= 1)
            return;
        
        double angle = Utils.getAngle(p.getLocation().getDirection(), e.getEntity().getLocation().getDirection());
        int level = plugin.getLevel(p, getLevelSys());
        
        if (angle < maxAngle.getValue(level) && (!onSneak || p.isSneaking()))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            ShadowEffectSkillEvent event = new ShadowEffectSkillEvent(p, e.getEntity(), this);
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            if (event.isCancelled())
                return;
            
            if (Math.random() <= chance.getValue(level))
                ((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(type, duration.getIntValue(level), strength.getIntValue(level)));
            
            setOnCooldown(p);
        }
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".type", "effect");
        config.set(getName() + ".effectType", type.getName());
        config.set(getName() + ".chance", chance.getInput());
        config.set(getName() + ".maxAngle", maxAngle.getInput());
        config.set(getName() + ".duration", duration.getInput());
        config.set(getName() + ".strength", strength.getInput());
        config.set(getName() + ".onSneak", onSneak);
    }
    
}
