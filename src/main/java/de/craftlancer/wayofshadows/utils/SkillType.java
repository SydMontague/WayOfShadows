package de.craftlancer.wayofshadows.utils;

public enum SkillType
{
    AIRASSASSINATION,
    BACKSTAB,
    CHESTTHIEF,
    EFFECT,
    GRAPPLINGHOOK,
    LOCKPICK,
    PICKPOCKET;
    
    public String getName()
    {
        return name();
    }
    
    public static SkillType matchType(String string)
    {
        for (SkillType type : values())
            if (type.name().equalsIgnoreCase(string))
                return type;
        
        return null;
    }
}
