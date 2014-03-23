package de.craftlancer.wayofshadows.skills;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import de.craftlancer.wayofshadows.ValueCatalogue;
import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.event.EndReason;
import de.craftlancer.wayofshadows.event.FailReason;
import de.craftlancer.wayofshadows.event.ShadowPickPocketEndEvent;
import de.craftlancer.wayofshadows.event.ShadowPickPocketEvent;
import de.craftlancer.wayofshadows.event.ShadowPickPocketFailEvent;
import de.craftlancer.wayofshadows.utils.PickPocketCheckTask;
import de.craftlancer.wayofshadows.utils.Utils;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

//TODO add damage value support
//TODO steal money?
/**
 * Represents a configuration of the PickPocket skill
 */
public class PickPocket extends Skill
{
    private ValueWrapper maxAngle;
    private ValueWrapper maxDistance;
    private ValueWrapper chance;
    private ValueWrapper maxValue;
    private boolean onSneak;
    private ValueCatalogue valueCatalogue;
    private boolean abortOnDamage;
    
    private String maxValueMsg;
    private String cantStealMsg;
    private String stealFailedMsg;
    private String attemptStealMsg;
    
    public PickPocket(WayOfShadows plugin, String key)
    {
        super(plugin, key);
        FileConfiguration config = plugin.getConfig();
        
        maxAngle = new ValueWrapper(config.getString(key + ".maxAngle", "90"));
        chance = new ValueWrapper(config.getString(key + ".chance", "1"));
        maxValue = new ValueWrapper(config.getString(key + ".maxValue", "90"));
        maxDistance = new ValueWrapper(config.getString(key + ".maxDistance", "10"));
        onSneak = config.getBoolean(key + ".onSneak", true);
        abortOnDamage = config.getBoolean(key + ".abortOnDamage", true);
        valueCatalogue = plugin.getValueCatalogue(config.getString(key + ".valueCatalogue"));
        
        if(valueCatalogue == null)
        {
            plugin.getLogger().severe("The given valueCatalogue for " + key + " is invalid!");
            plugin.getLogger().severe("This WILL throw errors in your console when someone tries to use this skill!");
        }
        
        maxValueMsg = config.getString(key + ".maxValueMsg", "You've reached your stealing limit.");
        cantStealMsg = config.getString(key + ".cantStealMsg", "You can't steal this Item.");
        stealFailedMsg = config.getString(key + ".stealFailedMsg", "You failed in stealing this item.");
        attemptStealMsg = config.getString(key + ".attemptStealMsg", "Is there something moving in your pockets?");
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
        
        if ((!onSneak || p.isSneaking()) && angle < maxAngle.getValue(level))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            p.openInventory(victim.getInventory());
            p.setMetadata("stealingPlayer." + getName(), new FixedMetadataValue(plugin, victim));
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
    protected void registerPermissions()
    {
        super.registerPermissions();
        plugin.getServer().getPluginManager().addPermission(new Permission("shadow." + getName() + ".exempt", PermissionDefault.FALSE));
    }
    
    @Override
    public void unregisterPermissions()
    {
        super.unregisterPermissions();
        plugin.getServer().getPluginManager().removePermission("shadow." + getName() + ".exempt");
    }
    
    private boolean hasPermission(Player p, ItemStack item, Player victim)
    {
        return super.hasPermission(p, item) && !victim.hasPermission("shadow." + getName() + ".exempt");
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSteal(InventoryClickEvent e)
    {
        Player p = ((Player) e.getWhoClicked());
        
        if (!p.hasMetadata("stealingPlayer." + getName()))
            return;
        
        e.setCancelled(true);
        
        if ((e.getRawSlot() >= e.getView().getTopInventory().getSize()) || e.getRawSlot() == -999)
            return;
        
        ItemStack item = e.getCurrentItem().clone();
        item.setAmount(1);
        
        if (item.getType().equals(Material.AIR))
            return;
        
        if (!valueCatalogue.canSteal(item))
        {
            p.sendMessage(cantStealMsg);
            plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketFailEvent(p, this, (Player) p.getMetadata("stealingPlayer." + getName()).get(0).value(), FailReason.UNSTEALABLE));
            return;
        }
        
        int level = plugin.getLevel(p, getLevelSys());
        int value = valueCatalogue.getValue(item);
        
        if (p.getMetadata("stealingValue." + getName()).get(0).asDouble() + value > maxValue.getIntValue(level))
        {
            p.sendMessage(maxValueMsg);
            plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketFailEvent(p, this, (Player) p.getMetadata("stealingPlayer." + getName()).get(0).value(), FailReason.UNSTEALABLE));
            return;
        }
        
        if (Math.random() < chance.getValue(level))
        {
            ShadowPickPocketEvent event = new ShadowPickPocketEvent(p, this, (Player) p.getMetadata("stealingPlayer." + getName()).get(0).value(), item);
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            if (event.isCancelled())
            {
                plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketFailEvent(p, this, (Player) p.getMetadata("stealingPlayer." + getName()).get(0).value(), FailReason.CANCELLED));
                return;
            }
            
            e.getView().getTopInventory().removeItem(item);
            p.getInventory().addItem(item);
        }
        else
        {
            plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketFailEvent(p, this, (Player) p.getMetadata("stealingPlayer." + getName()).get(0).value(), FailReason.UNSTEALABLE));
            
            if (stealFailedMsg.isEmpty())
                p.sendMessage(stealFailedMsg);
            if (attemptStealMsg.isEmpty())
                p.sendMessage(attemptStealMsg);
        }
        
        p.setMetadata("stealingValue." + getName(), new FixedMetadataValue(plugin, p.getMetadata("stealingValue." + getName()).get(0).asDouble() + value));
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClose(InventoryCloseEvent e)
    {
        if (!e.getPlayer().hasMetadata("stealingPlayer." + getName()))
            return;
        
        plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketEndEvent((Player) e.getPlayer(), this, (Player) e.getPlayer().getMetadata("stealingPlayer." + getName()).get(0).value(), EndReason.CLOSEINVENTORY));
        
        e.getPlayer().removeMetadata("stealingPlayer." + getName(), plugin);
        e.getPlayer().removeMetadata("stealingValue." + getName(), plugin);
        
        setOnCooldown((Player) e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e)
    {
        if (abortOnDamage && e.getEntity().hasMetadata("stealingPlayer." + getName()) && e.getEntity().getType().equals(EntityType.PLAYER))
        {
            ((Player) e.getEntity()).closeInventory();
            plugin.getServer().getPluginManager().callEvent(new ShadowPickPocketEndEvent((Player) e.getEntity(), this, (Player) e.getEntity().getMetadata("stealingPlayer." + getName()).get(0).value(), EndReason.PVP));
        }
    }
    
    @Override
    public void save(FileConfiguration config)
    {
        super.save(config);
        
        config.set(getName() + ".type", "pickpocket");
        config.set(getName() + ".chance", chance.getInput());
        config.set(getName() + ".maxValue", maxValue.getInput());
        config.set(getName() + ".maxAngle", maxAngle.getInput());
        config.set(getName() + ".onSneak", onSneak);
        config.set(getName() + ".valueCatalogue", valueCatalogue.getCatalogueName());
        
        config.set(getName() + ".maxValueMsg", maxValueMsg);
        config.set(getName() + ".cantStealMsg", cantStealMsg);
        config.set(getName() + ".stealFailedMsg", stealFailedMsg);
        config.set(getName() + ".attemptStealMsg", attemptStealMsg);
    }
    
    @Override
    public String getType()
    {
        return "pickpocket";
    }
}
