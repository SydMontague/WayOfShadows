package de.craftlancer.wayofshadows.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.skills.NewLockpickSkill.PickResult;
import de.craftlancer.wayofshadows.skills.NewLockpickSkill.Solution;
import de.craftlancer.wayofshadows.utils.SkillType;

public class NewLockpickSkill extends Skill
{
    private final Random rand = new Random();
    private List<Material> materials;
    
    private Map<Material, Integer> matToSize = new HashMap<Material, Integer>();
    private Map<UUID, LockpickSession> sessions = new HashMap<UUID, LockpickSession>();
    
    private Map<Location, Solution> solutionMap = new HashMap<Location, Solution>();
    
    public NewLockpickSkill(WayOfShadows instance, String key)
    {
        super(instance, key);
        materials = new ArrayList<Material>();
        materials.add(Material.IRON_INGOT);
        materials.add(Material.GOLD_INGOT);
        materials.add(Material.REDSTONE);
        
        matToSize.put(Material.WOODEN_DOOR, 4);
        matToSize.put(Material.CHEST, 4);
        matToSize.put(Material.FURNACE, 7);
        // TODO Auto-generated constructor stub
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

        if (!solutionMap.containsKey(e.getClickedBlock().getLocation()))
            solutionMap.put(e.getClickedBlock().getLocation(), new Solution(matToSize.get(e.getClickedBlock().getType())));

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
            sessions.remove(event.getPlayer().getUniqueId());
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
        TRUE(DyeColor.GREEN),
        POSITION(DyeColor.YELLOW), 
        FALSE(DyeColor.WHITE);

        private DyeColor color;
        
        private PickResult(DyeColor color)
        {
            this.color = color;
        }
        
        public ItemStack getWoolItem()
        {
            ItemStack item = new ItemStack(Material.WOOL);
            item.setDurability(color.getWoolData());
            
            return item;
        }
    }
}

class LockpickSession
{
    private NewLockpickSkill skill;
    private Solution solution;
    private Inventory inventory;
    
    public LockpickSession(NewLockpickSkill skill, Player player, Solution solution, Block block)
    {
        this.skill = skill;
        this.solution = solution;
        this.inventory = Bukkit.createInventory(null, 27, "Lockpick");
        
        /*
         * 0000000##
         * #########
         * 0000000#B
         */
        
        for (int i = 0; i < 27; i++)
        {
            if (i < solution.getSize())
                continue;
            
            if (i >= 18 && i - 18 < solution.getSize())
                inventory.setItem(i, new ItemStack(Material.WOOL));
            else if (i == 26)
                inventory.setItem(i, new ItemStack(Material.SLIME_BALL));
            else
                inventory.setItem(i, new ItemStack(Material.IRON_FENCE));
        }
        
        inventory.setMaxStackSize(1);
        player.openInventory(getInventory());
    }
    
    public void handleInventoryInteract(InventoryClickEvent event)
    {
        if(!event.getInventory().equals(getInventory()))
            return;
        
        if(event.getRawSlot() == 26)
            execute();
        
        if(event.getRawSlot() >= solution.getSize() && event.getRawSlot() < 27)
            event.setCancelled(true);
        else
        {
            
        }
    }
    
    private void execute()
    {
        Material[] input = new Material[solution.getSize()];
        
        for(int i = 0; i < solution.getSize(); i++)
        {
            ItemStack item = inventory.getItem(i);
            input[i] = item != null ? item.getType() : Material.AIR;
        }
        
        PickResult[] result = solution.getPickResult(input);
        
        for(int i = 0; i < solution.getSize(); i++)
            inventory.setItem(i + 18, result[i].getWoolItem());
        
        
    }
    
    public Inventory getInventory()
    {
        return inventory;
    }
}
