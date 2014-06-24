package de.craftlancer.wayofshadows.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;
import de.craftlancer.wayofshadows.utils.ValueWrapper;

public class NewLockpickSkill extends Skill
{
    private final Random rand = new Random();
    private final List<Material> materials = new ArrayList<Material>();
    
    private final boolean useFalseOnFail;
    private final boolean usePositionOnFail;
    private final boolean useTrueOnFalse;
    private final boolean useOnSuccess;
    
    private final ValueWrapper closeCooldown;
    
    private final String closeCooldownMsg;
    
    private final Map<Material, ValueWrapper> matToSize = new HashMap<Material, ValueWrapper>();
    
    private Map<UUID, LockpickSession> sessions = new HashMap<UUID, LockpickSession>();
    private Map<Location, Solution> solutionMap = new HashMap<Location, Solution>();
    
    private Map<Block, Long> closeMap = new HashMap<Block, Long>();
    
    public NewLockpickSkill(WayOfShadows instance, String key)
    {
        super(instance, key);
        
        for (String s : instance.getConfig().getStringList(key + ".materials"))
        {
            Material mat = Material.matchMaterial(s);
            
            if (mat == null)
                instance.getLogger().warning("An item is not a valid Material: " + s);
            else
                materials.add(mat);
        }
        
        useFalseOnFail = instance.getConfig().getBoolean(key + ".useFalseOnFail", true);
        usePositionOnFail = instance.getConfig().getBoolean(key + ".usePositionOnFail", true);
        useTrueOnFalse = instance.getConfig().getBoolean(key + ".useTrueOnFalse", true);
        useOnSuccess = instance.getConfig().getBoolean(key + ".useOnSuccess", true);
        
        closeCooldown = new ValueWrapper(instance.getConfig().getString(key + ".closeCooldown"));
        closeCooldownMsg = instance.getConfig().getString(key + ".closeCooldownMsg", "You can't close this for another %time% seconds!");
        
        if (instance.getConfig().getConfigurationSection(key + ".pickable") != null)
            for (String s : instance.getConfig().getConfigurationSection(key + ".pickable").getKeys(false))
            {
                Material mat = Material.getMaterial(s);
                if (mat == null)
                    continue;
                
                matToSize.put(mat, new ValueWrapper(instance.getConfig().getString(key + ".pickable." + s)));
            }
    }
    
    @Override
    public SkillType getType()
    {
        return SkillType.NEWLOCKPICK;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock() || !e.isCancelled() || e.getAction() != Action.RIGHT_CLICK_BLOCK || !matToSize.containsKey(e.getClickedBlock().getType()))
            return;
        
        ItemStack item = e.getItem();
        Player p = e.getPlayer();
        
        if (!isSkillItem(item) || !hasPermission(p, item))
            return;
        
        if (isOnCooldown(p))
        {
            p.sendMessage(getCooldownMsg(p));
            return;
        }
        
        int level = plugin.getLevel(p, getLevelSys());
        
        if (!solutionMap.containsKey(e.getClickedBlock().getLocation()))
            solutionMap.put(e.getClickedBlock().getLocation(), new Solution(matToSize.get(e.getClickedBlock().getType()).getIntValue(level)));
        
        sessions.put(e.getPlayer().getUniqueId(), new LockpickSession(this, e.getPlayer(), solutionMap.get(e.getClickedBlock().getLocation()), e.getClickedBlock()));
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!sessions.containsKey(event.getWhoClicked().getUniqueId()))
            return;
        
        LockpickSession session = sessions.get(event.getWhoClicked().getUniqueId());
        
        if (!session.getInventory().equals(event.getInventory()))
            return;
        
        session.handleInventoryInteract(event);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        if (sessions.containsKey(event.getPlayer().getUniqueId()))
            sessions.remove(event.getPlayer().getUniqueId()).handleInventoryClose(event);
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDoorInteract(PlayerInteractEvent e)
    {
        if (!e.hasBlock())
            return;
        
        switch (e.getClickedBlock().getType())
        {
            case WOODEN_DOOR:
            case TRAP_DOOR:
            case FENCE_GATE:
            case LEVER:
                break;
            default:
                return;
        }
        
        if (!closeMap.containsKey(e.getClickedBlock()))
            return;
        
        long time = closeMap.get(e.getClickedBlock());
        time = time - System.currentTimeMillis();
        
        if (time <= 0)
        {
            closeMap.remove(e.getClickedBlock());
            return;
        }
        
        e.setCancelled(true);
        e.getPlayer().sendMessage(closeCooldownMsg.replace("%time%", String.valueOf(time / 1000)));
    }
    
    public boolean isUseOnFalse()
    {
        return useFalseOnFail;
    }
    
    public boolean isUseOnPosition()
    {
        return usePositionOnFail;
    }
    
    public boolean isUseOnTrue()
    {
        return useTrueOnFalse;
    }
    
    public boolean useOnSuccess()
    {
        return useOnSuccess;
    }
    
    public void removeSolution(Location location)
    {
        solutionMap.remove(location);
    }
    
    public void addToCloseMap(Player player, Block block)
    {
        int level = plugin.getLevel(player, getLevelSys());
        
        closeMap.put(block, System.currentTimeMillis() + (long) closeCooldown.getValue(level) * 1000);
    }
    
    class Solution
    {
        private Material[] mats;
        
        public Solution(int size)
        {
            mats = new Material[size];
            
            for (int i = 0; i < size; i++)
                mats[i] = materials.get(rand.nextInt(materials.size()));
        }
        
        public int getSize()
        {
            return mats.length;
        }
        
        public Material getSlot(int slot)
        {
            if (slot > mats.length)
                throw new IllegalArgumentException("The given slot " + slot + " does not exist! Max slot: " + slot);
            
            return mats[slot];
        }
        
        public boolean compareSlot(int slot, Material mat)
        {
            if (slot > mats.length)
                throw new IllegalArgumentException("The given slot " + slot + " does not exist! Max slot: " + slot);
            
            return mats[slot] == mat;
        }
        
        public PickResult[] getPickResult(Material... input)
        {
            PickResult[] result = new PickResult[mats.length];
            
            Map<Material, Integer> remaining = new HashMap<Material, Integer>();
            
            for (Material mat : mats)
                if (!remaining.containsKey(mat))
                    remaining.put(mat, 1);
                else
                    remaining.put(mat, remaining.get(mat) + 1);
            
            for (int i = 0; i < mats.length; i++)
                result[i] = PickResult.FALSE;
            
            if (input.length != mats.length)
                return result;
            
            for (int i = 0; i < mats.length; i++)
                if (mats[i] == input[i])
                {
                    result[i] = PickResult.TRUE;
                    int left = remaining.get(mats[i]) - 1;
                    
                    if (left > 0)
                        remaining.put(mats[i], left);
                    else
                        remaining.remove(mats[i]);
                }
            
            for (int i = 0; i < mats.length; i++)
                if (result[i] == PickResult.FALSE && remaining.containsKey(input[i]))
                {
                    result[i] = PickResult.POSITION;
                    int left = remaining.get(input[i]) - 1;
                    
                    if (left > 0)
                        remaining.put(input[i], left);
                    else
                        remaining.remove(input[i]);
                }
            
            return result;
        }
    }
    
    enum PickResult
    {
        TRUE(DyeColor.GREEN, ChatColor.GREEN),
        POSITION(DyeColor.YELLOW, ChatColor.YELLOW),
        FALSE(DyeColor.WHITE, ChatColor.WHITE);
        
        private DyeColor color;
        private ChatColor chatColor;
        
        private PickResult(DyeColor color, ChatColor chatColor)
        {
            this.color = color;
            this.chatColor = chatColor;
        }
        
        @SuppressWarnings("deprecation")
        public ItemStack getWoolItem()
        {
            ItemStack item = new ItemStack(Material.WOOL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(chatColor + name());
            item.setItemMeta(meta);
            item.setDurability(color.getWoolData());
            
            return item;
        }
    }
}
