package com.justinwflory.ritcommandcenter;

import org.bukkit.plugin.java.JavaPlugin;

public class RitCommandCenter
        extends JavaPlugin
{
    public void onEnable()
    {
        getCommand("rit").setExecutor(new RitCommand());
    }
}
