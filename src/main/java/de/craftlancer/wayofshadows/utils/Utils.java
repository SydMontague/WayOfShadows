package de.craftlancer.wayofshadows.utils;

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
    
}
