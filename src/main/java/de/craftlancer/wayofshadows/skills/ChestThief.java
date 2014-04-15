package de.craftlancer.wayofshadows.skills;

import de.craftlancer.wayofshadows.WayOfShadows;
import de.craftlancer.wayofshadows.utils.SkillType;

public class ChestThief extends Skill
{

    public ChestThief(WayOfShadows instance, String key)
    {
        super(instance, key);
        // TODO Auto-generated constructor stub
    }

    @Override
    public SkillType getType()
    {
        return SkillType.CHESTTHIEF;
    }
    
}
