package com.oreonk;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
    private static main instance;
    @Override
    public void onEnable() {
        instance = this;
        System.out.println("Combatlog...");
        getServer().getPluginManager().registerEvents(new kill(), this);
    }

    @Override
    public void onDisable() {

    }
    public static main getInstance(){
        return instance;
    }
}
