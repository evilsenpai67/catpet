package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RitualManager {
    private final CatPets plugin;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private final Map<UUID, RitualData> activeRituals = new ConcurrentHashMap<>();

    public RitualManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }

    public boolean startRitual(Player target, CatType catType) {
        if (activeRituals.containsKey(target.getUniqueId())) {
            return false;
        }
        
        int duration = configManager.getConfig().getInt("ritual.duration", 3600) * 20; // in ticks
        
        RitualData ritual = new RitualData(target, catType, duration);
        activeRituals.put(target.getUniqueId(), ritual);
        
        messageManager.broadcast("ritual-started", "player", target.getName(), "cat", catType.getDisplayName());
        
        ritual.task.runTaskTimer(plugin, 0L, 20L);
        return true;
    }

    public void cancelRitual(UUID playerId) {
        RitualData ritual = activeRituals.remove(playerId);
        if (ritual != null) {
            ritual.task.cancel();
            if (ritual.bossBar != null) {
                ritual.bossBar.removeAll();
            }
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                messageManager.sendMessage(player, "ritual-cancelled");
            }
        }
    }

    public void cancelAllRituals() {
        for (UUID uuid : new java.util.ArrayList<>(activeRituals.keySet())) {
            cancelRitual(uuid);
        }
    }

    public boolean hasActiveRitual(UUID playerId) {
        return activeRituals.containsKey(playerId);
    }

    public RitualData getRitual(UUID playerId) {
        return activeRituals.get(playerId);
    }

    private class RitualData {
        final Player player;
        final CatType catType;
        final int totalDuration;
        int remainingTicks;
        final BukkitRunnable task;
        BossBar bossBar;

        RitualData(Player player, CatType catType, int duration) {
            this.player = player;
            this.catType = catType;
            this.totalDuration = duration;
            this.remainingTicks = duration;
            
            if (configManager.getConfig().getBoolean("ritual.taskbar.enabled", true)) {
                String title = configManager.getConfig().getString("ritual.taskbar.title", "&6&lTier 3 Ritual in Progress");
                this.bossBar = Bukkit.createBossBar(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', title), BarColor.YELLOW, BarStyle.SEGMENTED_10);
                this.bossBar.addPlayer(player);
            }
            
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || remainingTicks <= 0) {
                        completeRitual();
                        return;
                    }
                    
                    remainingTicks -= 20;
                    
                    if (bossBar != null) {
                        bossBar.setProgress((double) remainingTicks / totalDuration);
                    }
                    
                    if (configManager.getConfig().getBoolean("ritual.particles.enabled", true)) {
                        spawnParticles();
                    }
                    
                    int soundInterval = configManager.getConfig().getInt("ritual.sounds.interval", 300) * 20;
                    if (configManager.getConfig().getBoolean("ritual.sounds.enabled", true) && (totalDuration - remainingTicks) % soundInterval == 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
                    }
                }
            };
        }

        private void spawnParticles() {
            Location loc = player.getLocation().add(0, 1, 0);
            String particleType = configManager.getConfig().getString("ritual.particles.type", "ENCHANTMENT_TABLE");
            int count = configManager.getConfig().getInt("ritual.particles.count", 20);
            double radius = configManager.getConfig().getInt("ritual.particles.radius", 3);
            
            Particle particle = Particle.valueOf(particleType.toUpperCase());
            player.getWorld().spawnParticle(particle, loc, count, radius, 2, radius, 0.5);
        }

        private void completeRitual() {
            activeRituals.remove(player.getUniqueId());
            if (bossBar != null) {
                bossBar.removeAll();
            }
            
            if (!player.isOnline()) return;
            
            CatData catData = plugin.getCatManager().getCatData(player);
            if (catData != null && catData.getCatType() == catType) {
                catData.setTier(3);
                plugin.getCatManager().updateCatName(catData);
                plugin.getItemManager().createRitualCore(catType);
                messageManager.broadcast("ritual-completed", "player", player.getName(), "cat", catType.getDisplayName());
            }
        }
    }
}