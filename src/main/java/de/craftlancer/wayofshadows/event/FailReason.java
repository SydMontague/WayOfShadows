package de.craftlancer.wayofshadows.event;

/**
 * Represents the reason why a pickpocket failed.
 */
public enum FailReason
{
    CHANCE,
    MAXVALUE_REACHED,
    UNSTEALABLE,
    CANCELLED;
}
