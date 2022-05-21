package com.oreonk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class kill implements Listener {
    public static HashMap<UUID, Integer> delayedPlayers = new HashMap<>();
    static String[] blockedCommands = {
           "/spawn",
            "/home",
            "/tpa",
            "/tpaccept",
            "/fly",
            "/rtp",
            "/clan home",
            "/back",
            "/heal",
            "/auc",
            "/ah"
    };
    @EventHandler
    public void yeet(PlayerQuitEvent e){
        String playername = e.getPlayer().getName();
        Player player = e.getPlayer();
        if (delayedPlayers.containsKey(player.getUniqueId()) & !delayedPlayers.isEmpty()){
            player.setHealth(0);
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "Игрок " + ChatColor.WHITE + playername + ChatColor.RED +" вышел с сервера находясь в бою!");
        }
    }
    @EventHandler
    public void list(EntityDamageByEntityEvent event){
        main plugin = main.getPlugin(main.class);
        if(!event.getEntity().getWorld().getName().equals("spawn")) {
            if (event.getEntity() instanceof Player & event.getDamager() instanceof Player) {
                Player damaged = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                String damagedName = event.getEntity().getName();
                String damagerName = event.getDamager().getName();
                if (!delayedPlayers.containsKey(damager.getUniqueId()) || !delayedPlayers.containsKey(damaged.getUniqueId())) {
                    damaged.sendMessage(ChatColor.RED + "Вы были ударены " + damagerName + " не выходите с сервера!");
                    damager.sendMessage(ChatColor.RED + "Вы ударили " + damagedName + " не выходите с сервера!");
                    damaged.setFlying(false);
                    damager.setFlying(false);
                    damaged.setAllowFlight(false);
                    damager.setAllowFlight(false);
                }
                if (!delayedPlayers.containsKey(damager.getUniqueId())){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (delayedPlayers.containsKey(damager.getUniqueId())) {
                                int delay = delayedPlayers.get(damager.getUniqueId());
                                delay--;
                                damager.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Вы в бою! " + delay + " секунд осталось"));
                                delayedPlayers.replace(damager.getUniqueId(), delay);
                                if (delay <= 0) {
                                    damager.sendMessage(ChatColor.GREEN + "CombatLog более не действует.");
                                    delayedPlayers.remove(damager.getUniqueId());
                                    this.cancel();
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 1, 20);
                }
                if (!delayedPlayers.containsKey(damaged.getUniqueId())){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (delayedPlayers.containsKey(damaged.getUniqueId())) {
                                int dellay = delayedPlayers.get(damaged.getUniqueId());
                                dellay--;
                                damaged.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Вы в бою! " + dellay + " секунд осталось"));
                                delayedPlayers.replace(damaged.getUniqueId(), dellay);
                                if (dellay <= 0) {
                                    damaged.sendMessage(ChatColor.GREEN + "CombatLog более не действует.");
                                    delayedPlayers.remove(damaged.getUniqueId());
                                    this.cancel();
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 1, 20);
                }
                if (!delayedPlayers.containsKey(damaged.getUniqueId())) {
                    delayedPlayers.put(damaged.getUniqueId(), 10);
                }else if(delayedPlayers.containsKey(damaged.getUniqueId())){
                    delayedPlayers.replace(damaged.getUniqueId(), 10);
                }
                if (!delayedPlayers.containsKey(damager.getUniqueId())) {
                    delayedPlayers.put(damager.getUniqueId(), 10);
                }else if (delayedPlayers.containsKey(damager.getUniqueId())) {
                    delayedPlayers.replace(damager.getUniqueId(), 10);
                }
            }
            if (event.getEntity() instanceof Player) {
                Player damaged = (Player) event.getEntity();
                if (damaged.getGameMode().toString().equals("SURVIVAL")) {
                    ((Player) event.getEntity()).setFlying(false);
                    ((Player) event.getEntity()).setAllowFlight(false);
                }
            } else if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();
                if (damager.getGameMode().toString().equals("SURVIVAL")) {
                    ((Player) event.getDamager()).setFlying(false);
                    ((Player) event.getDamager()).setAllowFlight(false);
                }
            }
        }
    }
    @EventHandler
    public void commandBlock(PlayerCommandPreprocessEvent event){
        String message = event.getMessage();
        String[] args = message.split(" ");
        Player player = event.getPlayer();
        boolean blocked = false;
        for(String string : blockedCommands)
            if (args[0].toLowerCase().startsWith(string) & delayedPlayers.containsKey(player.getUniqueId()) & !delayedPlayers.isEmpty()) {
                blocked = true;
            }
        if(blocked){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Вы в бою!");
        }
    }
}
