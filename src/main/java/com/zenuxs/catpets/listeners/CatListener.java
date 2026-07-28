package com.zenuxs.catpets.listeners;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.managers.ItemManager;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CatListener implements Listener {
    private final CatPets plugin;

    public CatListener(CatPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getCatManager().onPlayerJoin(event.getPlayer());
        
        if (!plugin.getCatManager().hasCat(event.getPlayer())) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (event.getPlayer().isOnline()) {
                    plugin.getCatManager().spawnCat(event.getPlayer());
                }
            }, plugin.getConfigManager().getCatSpawnDelay() * 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getCatManager().onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Cat cat) {
            if (cat.getOwner() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        if (event.getRightClicked() instanceof Cat cat) {
            Player player = event.getPlayer();
            CatData catData = plugin.getCatManager().getCatData(player);
            
            if (catData != null && catData.getCatEntity() != null && catData.getCatEntity().equals(cat)) {
                ItemManager itemManager = plugin.getItemManager();
                if (itemManager.isUpgradeFish(player.getInventory().getItemInMainHand())) {
                    event.setCancelled(true);
                    int tier = itemManager.getUpgradeFishTier(player.getInventory().getItemInMainHand());
                    CatType fishCatType = itemManager.getUpgradeFishCatType(player.getInventory().getItemInMainHand());
                    
                    if (fishCatType != null && fishCatType == catData.getCatType()) {
                        if (catData.getTier() < tier) {
                            plugin.getCatManager().upgradeCat(player, tier);
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        } else {
                            plugin.getMessageManager().sendMessage(player, "max-tier");
                        }
                    } else {
                        plugin.getMessageManager().sendPrefixedMessage(player, "&cThis fish is for a different cat type!");
                    }
                }
            }
        }
    }
}