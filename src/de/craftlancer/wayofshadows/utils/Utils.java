package de.craftlancer.wayofshadows.utils;

import org.bukkit.util.Vector;

public class Utils
{
    
    public static double getAngle(Vector vec1, Vector vec2)
    {
        return vec1.angle(vec2) * 180 / Math.PI;
    }
    
}
