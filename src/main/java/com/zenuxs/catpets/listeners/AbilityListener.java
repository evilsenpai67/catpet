package com.zenuxs.catpets.listeners;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class AbilityListener implements Listener {
    private final CatPets plugin;

    public AbilityListener(CatPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            CatData catData = plugin.getCatManager().getCatData(player);
            if (catData != null) {
                plugin.getAbilityManager().onOwnerDamaged(event, catData);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker) {
            CatData catData = plugin.getCatManager().getCatData(attacker);
            if (catData != null) {
                plugin.getAbilityManager().onOwnerAttack(event, catData);
                plugin.getAbilityManager().onHitByOwner(attacker, event, catData);
                
                if (event.getEntity() instanceof LivingEntity target) {
                    plugin.getAbilityManager().onHitByCat(attacker, target, catData);
                }
            }
        }
        
        if (event.getEntity() instanceof Player victim) {
            CatData catData = plugin.getCatManager().getCatData(victim);
            if (catData != null) {
                plugin.getAbilityManager().onTargetDamaged(event, catData);
            }
        }
    }
}