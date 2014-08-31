package de.craftlancer.wayofshadows.skills;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO add events
public class Leap extends Skill
{
    private ValueWrapper maxDistance;
    private ValueWrapper minDistance;
    private ValueWrapper maxAngle;
    private ValueWrapper damage;
    
    public Leap(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "10"));
        maxDistance = new ValueWrapper(config.getString(key + ".maxDistance", "5"));
        minDistance = new ValueWrapper(config.getString(key + ".minDistance", "3"));
        damage = new ValueWrapper(config.getString(key + ".damage", "0.5*y"));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
                
        if(event.getAction() != Action.LEFT_CLICK_AIR)
            return;
        
        if(!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        if(isOnCooldown(p))
            return;

        LivingEntity victim = getVictim(p);
        
        if(victim == null)
            return;
        
        Location loc = p.getLocation();
        double distance = loc.distance(victim.getLocation());
        Vector vec = loc.getDirection().normalize();
        
        vec.multiply(distance - 1);
        vec.add(new Vector(0,0.5,0));

        int level = plugin.getLevel(p, getLevelSys());
        
        loc.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 0);
        loc.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        
        p.teleport(loc.add(vec), TeleportCause.PLUGIN);
        
        victim.damage(damage.getValue(level, Utils.getWeaponDamage(item)), p);
        
        loc.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 0);
        
        setOnCooldown(p);
    }
    
    private LivingEntity getVictim(Player player)
    {
        int level = plugin.getLevel(player, getLevelSys());

        double lMinDistance = minDistance.getValue(level);
        double lMaxDistance = maxDistance.getValue(level);
        double lMaxAngle = maxAngle.getValue(level);
        
        List<Entity> entity = player.getNearbyEntities(lMaxDistance, lMaxDistance, lMaxDistance);
        
        LivingEntity closest = null;
        double closestAngle = Double.MAX_VALUE;
        
        for(Entity en : entity)
        {
            if(!(en instanceof LivingEntity))
                continue;
            
            Location loc = player.getLocation();
            Location loc2 = en.getLocation();

            double localDistance = loc.distance(loc2);

            if(localDistance > lMaxDistance || localDistance < lMinDistance)
                continue;
            
            Vector vec1 = loc.getDirection();
            Vector vec2 = new Vector(loc2.getX() - loc.getX(), loc2.getY() - loc.getY(), loc2.getZ() - loc.getZ());
            
            double angle = Utils.getAngle(vec1, vec2);
            
            if(angle > closestAngle)
                continue;
            
            if(angle > lMaxAngle)
                continue;
            
            closest = (LivingEntity) en;
            closestAngle = angle;
        }
        
        return closest;
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.SHADOWLEAP;
    }
    
}
