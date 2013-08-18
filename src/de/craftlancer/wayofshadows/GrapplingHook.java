package de.craftlancer.wayofshadows;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import de.craftlancer.wayofshadows.event.ShadowPullEvent;

public class GrapplingHook extends Skill
{
    private ValueWrapper blockTime;
    private ValueWrapper maxDistance;
    private ValueWrapper distanceToInitial;
    private ValueWrapper itemsPerBlock;
    
    private String levelSystem;
    
    private List<String> pullLore;
    private List<Integer> pullItems;
    private List<String> pullItemNames;
    
    private String distanceMsg;
    private String initialMsg;
    private String errorMsg;
    
    public GrapplingHook(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = instance.getConfig();
        
        pullLore = config.getStringList(key + ".pullLore");
        pullItemNames = config.getStringList(key + ".pullItemNames");
        pullItems = config.getIntegerList(key + ".pullItems");
        
        distanceMsg = config.getString(key + ".distanceMsg");
        initialMsg = config.getString(key + ".initialMsg");
        errorMsg = config.getString(key + ".errorMsg");
        
        levelSystem = config.getString(key + ".levelSystem");
        
        blockTime = new ValueWrapper(config.getString(key + ".blockTime", "0"));
        maxDistance = new ValueWrapper(config.getString(key + ".maxDistance", "0"));
        distanceToInitial = new ValueWrapper(config.getString(key + ".distanceToInitial", "0"));
        itemsPerBlock = new ValueWrapper(config.getString(key + ".itemsPerBlock", "0"));
    }
    
    public GrapplingHook(WayOfShadows instance, String key, int hook, int pull, long bTime, int maxDist, int initDistance, double ipb, String string, String string2, String string3)
    {
        super(instance, key, String.valueOf(hook));
        
        pullLore = new ArrayList<String>();
        pullItemNames = new ArrayList<String>();
        pullItems = new ArrayList<Integer>();
        pullItems.add(pull);
        
        distanceMsg = string3;
        initialMsg = string2;
        errorMsg = string;
        
        blockTime = new ValueWrapper(bTime);
        maxDistance = new ValueWrapper(maxDist);
        distanceToInitial = new ValueWrapper(initDistance);
        itemsPerBlock = new ValueWrapper(ipb);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onHookShoot(PlayerInteractEvent event)
    {
        if (!event.hasItem() || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        
        Player player = event.getPlayer();
        
        if (isSkillItem(event.getItem()) && hasPermission(player, event.getItem()))
        {
            Arrow arrow = player.launchProjectile(Arrow.class);
            ItemStack item = event.getItem().clone();
            item.setAmount(1);
            
            arrow.setMetadata("playerLocation", new FixedMetadataValue(plugin, player.getLocation()));
            player.setMetadata("hookArrow", new FixedMetadataValue(plugin, arrow));
            
            player.getInventory().removeItem(new ItemStack(item));
        }
    }
    
    @EventHandler
    public void onPull(PlayerInteractEvent e)
    {
        if (!e.hasItem() || !isPullItem(e.getItem()) || !(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR))
            return;
        
        Player player = e.getPlayer();
        
        if (!player.hasMetadata("hookArrow") || player.hasMetadata("teleportArrow") || !((Arrow) player.getMetadata("hookArrow").get(0).value()).hasMetadata("isHit"))
            return;
        
        Arrow arrow = (Arrow) player.getMetadata("hookArrow").get(0).value();
        Location initLoc = (Location) arrow.getMetadata("playerLocation").get(0).value();
        
        ShadowPullEvent event = new ShadowPullEvent(player, arrow);
        Bukkit.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        e.setCancelled(true);
        
        Location ploc = player.getEyeLocation();
        final Location aloc = arrow.getLocation();
        double distance1 = ploc.distance(aloc);
        int level = plugin.getSkillLevels() != null ? plugin.getSkillLevels().getUserLevel(levelSystem, player.getName()) : 0;
        int amount = (int) Math.ceil(distance1 * itemsPerBlock.getValue(level));
        ItemStack item = e.getItem().clone();
        item.setAmount(amount);
        
        if (distance1 > maxDistance.getIntValue(level))
        {
            player.sendMessage(distanceMsg);
            return;
        }
        
        if (ploc.distance(initLoc) > distanceToInitial.getIntValue(level))
        {
            player.sendMessage(initialMsg);
            return;
        }
        
        player.teleport(initLoc);
        Arrow ball = player.launchProjectile(Arrow.class);
        
        ball.setMetadata("teleportArrow", new FixedMetadataValue(plugin, true));
        player.setMetadata("teleportArrow", new FixedMetadataValue(plugin, ball.getEntityId()));
        
        player.getInventory().removeItem(new ItemStack(item));
        
    }
    
    @EventHandler
    public void onHookHit(ProjectileHitEvent event)
    {
        if (!event.getEntity().getType().equals(EntityType.ARROW) || !event.getEntity().getShooter().getType().equals(EntityType.PLAYER))
            return;
        
        Player player = (Player) event.getEntity().getShooter();
        Arrow arrow = (Arrow) event.getEntity();
        
        if (arrow.hasMetadata("playerLocation"))
        {
            arrow.setMetadata("isHit", new FixedMetadataValue(plugin, true));
            return;
        }
        
        if (player.hasMetadata("teleportArrow") && (arrow.getEntityId() == player.getMetadata("teleportArrow").get(0).asInt()))
        {
            Location loc = arrow.getLocation();
            arrow.remove();
            
            if ((loc.getBlock().getType().isSolid() || loc.getBlock().getRelative(0, 1, 0).getType().isSolid() || loc.getBlock().getRelative(0, 2, 0).getType().isSolid()))
            {
                player.sendMessage(errorMsg);
                player.removeMetadata("teleportArrow", plugin);
                player.removeMetadata("hookArrow", plugin);
                return;
            }
            
            player.teleport(loc);
            
            int level = plugin.getSkillLevels() != null ? plugin.getSkillLevels().getUserLevel(levelSystem, player.getName()) : 0;
            final Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
            
            if (!block.getType().isSolid())
            {
                final Material type = block.getType();
                block.setType(Material.GLASS);
                
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        block.setType(type);
                    }
                }.runTaskLater(plugin, blockTime.getIntValue(level));
            }
            
            player.setFallDistance(0);
            player.removeMetadata("teleportArrow", plugin);
            player.removeMetadata("hookArrow", plugin);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (!event.getDamager().getType().equals(EntityType.ARROW))
            return;
        
        Arrow arrow = (Arrow) event.getDamager();
        
        if (arrow.hasMetadata("teleportArrow") || arrow.hasMetadata("playerLocation"))
            event.setCancelled(true);
    }
    
    private boolean isPullItem(ItemStack item)
    {
        if (pullItems.contains(item.getTypeId()))
            return true;
        
        if (item.hasItemMeta())
            if (item.getItemMeta().hasDisplayName() && pullItemNames.contains(item.getItemMeta().getDisplayName()))
                return true;
            else if (item.getItemMeta().hasLore())
                for (String str : pullLore)
                    for (String str2 : item.getItemMeta().getLore())
                        if (str2.contains(str))
                            return true;
        
        return false;
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".type", "grapplinghook");
        config.set(getName() + ".pullLore", pullLore);
        config.set(getName() + ".pullItems", pullItems);
        config.set(getName() + ".pullItemNames", pullItemNames);
        
        config.set(getName() + ".distanceMsg", distanceMsg);
        config.set(getName() + ".initialMsg", initialMsg);
        config.set(getName() + ".errorMsg", errorMsg);
        config.set(getName() + ".levelSystem", levelSystem);
        
        config.set(getName() + ".blockTime", blockTime.getInput());
        config.set(getName() + ".maxDistance", maxDistance.getInput());
        config.set(getName() + ".distanceToInitial", distanceToInitial.getInput());
        config.set(getName() + ".itemsPerBlock", itemsPerBlock.getInput());
    }
}
