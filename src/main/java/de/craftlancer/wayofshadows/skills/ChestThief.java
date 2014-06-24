package de.craftlancer.wayofshadows.skills;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

public class ChestThief extends ThiefSkill
{
    private ValueWrapper openChance;
    
    private Map<Material, ValueWrapper> openMod = new HashMap<Material, ValueWrapper>();
    
    private String failedToOpenMsg;
    
    public ChestThief(WayOfShadows instance, String key)
    {
        super(instance, key);
        FileConfiguration config = plugin.getConfig();
        openChance = new ValueWrapper(config.getString(key + ".openChance", "y"));
        
        if (config.getConfigurationSection(key + ".openable") != null)
            for (String s : config.getConfigurationSection(key + ".openable").getKeys(false))
            {
                Material mat = Material.getMaterial(s);
                if (mat == null)
                    continue;
                                
                openMod.put(mat, new ValueWrapper(config.getString(key + ".openable." + s)));
            }
        
        failedToOpenMsg = config.getString(key + ".failedToOpenMsg", "You failed to open that inventory.");
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.CHESTTHIEF;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
                
        if (!event.hasBlock())
            return;

        if (!event.isCancelled())
            return;

        if (!(event.getClickedBlock().getState() instanceof InventoryHolder))
            return;

        Inventory victim = ((InventoryHolder) event.getClickedBlock().getState()).getInventory();
        
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;

        int level = plugin.getLevel(p, getLevelSys());
        
        Material type = event.getClickedBlock().getType();
        
        if (!openMod.containsKey(type))
            return;

        if (Math.random() < openChance.getValue(level, openMod.get(type).getValue(level)))
        {
            p.sendMessage(getFailedToOpenMsg());
            return;
        }

        if ((!getOnSneak() || p.isSneaking()))
        {
            if (isOnCooldown(p))
            {
                p.sendMessage(getCooldownMsg(p));
                return;
            }
            
            Bukkit.getLogger().info("1");
            p.openInventory(victim);
            p.setMetadata("stealingPlayer." + getName(), new FixedMetadataValue(plugin, victim));
            p.setMetadata("stealingValue." + getName(), new FixedMetadataValue(plugin, 0));
        }
    }
    
    private String getFailedToOpenMsg()
    {
        return failedToOpenMsg;
    }
}
