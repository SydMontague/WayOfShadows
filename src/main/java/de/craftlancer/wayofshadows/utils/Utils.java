package de.craftlancer.wayofshadows.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Utils
{
    /**
     * Get the angle in degree between 2 vectors
     * 
     * @param vec1 - the 1st vector
     * @param vec2 - the 2nd vector
     * @return the angle in degree
     */
    public static double getAngle(Vector vec1, Vector vec2)
    {
        return vec1.angle(vec2) * 180 / Math.PI;
    }
    
    public static int getWeaponDamage(ItemStack item)
    {
        if(item == null)
            return 1;
        
        switch (item.getType())
        {
            case DIAMOND_SWORD:
                return 8;
                
            case IRON_SWORD:
            case DIAMOND_AXE:
                return 7;
                
            case STONE_SWORD:
            case IRON_AXE:
            case DIAMOND_PICKAXE:
                return 6;
                
            case DIAMOND_SPADE:
            case IRON_PICKAXE:
            case STONE_AXE:
            case WOOD_SWORD:
            case GOLD_SWORD:
                return 5;
                
            case WOOD_AXE:
            case GOLD_AXE:
            case STONE_PICKAXE:
            case IRON_SPADE:
                return 4;
                
            case WOOD_PICKAXE:
            case GOLD_PICKAXE:
            case STONE_SPADE:
                return 3;
                
            case WOOD_SPADE:
            case GOLD_SPADE:
                return 2;
                
            default:
                return 1;
        }
    }
    
}
