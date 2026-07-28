package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CatManager {
    private final CatPets plugin;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private final Map<UUID, CatData> playerCats = new ConcurrentHashMap<>();

    public CatManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
        startFollowTask();
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
        this.messageManager = plugin.getMessageManager();
    }

    public CatData spawnCat(Player player) {
        CatType catType = CatType.getRandom();
        CatData catData = new CatData(player.getUniqueId(), catType);
        
        Location spawnLoc = player.getLocation().add(1, 0, 1);
        Cat cat = (Cat) player.getWorld().spawnEntity(spawnLoc, EntityType.CAT);
        
        cat.setOwner(player);
        cat.setCustomNameVisible(true);
        cat.setCustomName(configManager.getPrefix() + catType.getDisplayName() + " &7(Tier 1)");
        cat.setVariant(catType.getEntityVariant());
        cat.setAdult();
        cat.setSitting(false);
        
        cat.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255, false, false));
        cat.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 255, false, false));
        cat.setInvulnerable(true);
        cat.setRemoveWhenFarAway(false);
        
        catData.setCatEntity(cat);
        playerCats.put(player.getUniqueId(), catData);
        
        messageManager.sendMessage(player, "cat-spawned", "cat", catType.getDisplayName());
        return catData;
    }

    public CatData getCatData(Player player) {
        return playerCats.get(player.getUniqueId());
    }

    public CatData getCatData(UUID uuid) {
        return playerCats.get(uuid);
    }

    public void despawnCat(Player player) {
        CatData catData = playerCats.remove(player.getUniqueId());
        if (catData != null && catData.getCatEntity() != null && catData.getCatEntity().isValid()) {
            catData.getCatEntity().remove();
        }
    }

    public void despawnAllCats() {
        for (CatData catData : playerCats.values()) {
            if (catData.getCatEntity() != null && catData.getCatEntity().isValid()) {
                catData.getCatEntity().remove();
            }
        }
        playerCats.clear();
    }

    public void upgradeCat(Player player, int newTier) {
        CatData catData = playerCats.get(player.getUniqueId());
        if (catData == null) return;
        
        catData.setTier(newTier);
        Cat cat = catData.getCatEntity();
        if (cat != null && cat.isValid()) {
            cat.setCustomName(configManager.getPrefix() + catData.getCatType().getDisplayName() + " &7(Tier " + newTier + ")");
        }
        messageManager.sendMessage(player, "cat-upgraded", "cat", catData.getCatType().getDisplayName(), "tier", String.valueOf(newTier));
    }

    public void updateCatName(CatData catData) {
        Cat cat = catData.getCatEntity();
        if (cat != null && cat.isValid()) {
            cat.setCustomName(configManager.getPrefix() + catData.getCatType().getDisplayName() + " &7(Tier " + catData.getTier() + ")");
        }
    }

    public boolean hasCat(Player player) {
        return playerCats.containsKey(player.getUniqueId());
    }

    private void startFollowTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CatData catData : playerCats.values()) {
                    Player owner = catData.getOwner();
                    Cat cat = catData.getCatEntity();
                    
                    if (owner == null || !owner.isOnline() || cat == null || !cat.isValid()) {
                        if (owner != null && owner.isOnline()) {
                            despawnCat(owner);
                        }
                        continue;
                    }
                    
                    if (cat.isSitting()) {
                        cat.setSitting(false);
                    }
                    
                    double distance = cat.getLocation().distance(owner.getLocation());
                    if (distance > 10) {
                        cat.teleport(owner.getLocation().add(1, 0, 1));
                    } else if (distance > 3) {
                        cat.getPathfinder().moveTo(owner, 1.5);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void onPlayerJoin(Player player) {
        CatData catData = playerCats.get(player.getUniqueId());
        if (catData != null && catData.getCatEntity() != null && catData.getCatEntity().isValid()) {
            catData.getCatEntity().setOwner(player);
        }
    }

    public void onPlayerQuit(Player player) {
    }
}