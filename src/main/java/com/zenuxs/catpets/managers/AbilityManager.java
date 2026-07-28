package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AbilityManager {
    private final CatPets plugin;
    private ConfigManager configManager;
    private TrustManager trustManager;
    private MessageManager messageManager;
    private final Set<UUID> immunePlayers = new HashSet<>();
    private final Set<UUID> invisPlayers = new HashSet<>();

    public AbilityManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.trustManager = plugin.getTrustManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
        this.trustManager = plugin.getTrustManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void triggerAbility1(CatData catData) {
        Player owner = catData.getOwner();
        if (owner == null) return;
        
        CatType type = catData.getCatType();
        int tier = catData.getTier();
        
        switch (type) {
            case JAMES -> triggerJamesAbility1(owner, tier);
            case BOB -> triggerBobAbility1(owner, tier);
            case LARRY -> triggerLarryAbility1(owner, tier);
        }
    }

    public void triggerAbility2(CatData catData) {
        Player owner = catData.getOwner();
        if (owner == null) return;
        
        CatType type = catData.getCatType();
        int tier = catData.getTier();
        
        switch (type) {
            case JAMES -> triggerJamesAbility2(owner, tier);
            case BOB -> triggerBobAbility2(owner, tier);
            case LARRY -> triggerLarryAbility2(owner, tier);
        }
    }

    private void triggerJamesAbility1(Player owner, int tier) {
        int baseDuration = configManager.getConfig().getInt("cats.james.tier1.ability1.duration", 5) * 20;
        int cooldown = configManager.getConfig().getInt("cats.james.tier1.ability1.cooldown", 30) * 1000;
        
        immunePlayers.add(owner.getUniqueId());
        owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, baseDuration, 255, false, false));
        
        if (tier >= 2) {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0, false, false));
            owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        }
        
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        owner.getWorld().spawnParticle(Particle.END_ROD, owner.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                immunePlayers.remove(owner.getUniqueId());
                if (tier < 2) {
                    owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                }
            }
        }.runTaskLater(plugin, baseDuration);
    }

    private void triggerJamesAbility2(Player owner, int tier) {
        int radius = configManager.getConfig().getInt("cats.james.tier1.ability2.radius", 7);
        int duration = configManager.getConfig().getInt("cats.james.tier1.ability2.duration", 10) * 20;
        int level = configManager.getConfig().getInt("cats.james.tier1.ability2.regen-level", 2) - 1;
        
        Location center = owner.getLocation();
        owner.getWorld().spawnParticle(Particle.HEART, center, 50, radius, 2, radius, 0.2);
        owner.getWorld().playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        
        for (Player p : center.getWorld().getPlayers()) {
            if (p.getLocation().distance(center) <= radius) {
                if (trustManager.isTrusted(owner, p) || p.equals(owner)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, level, false, false));
                    if (tier >= 2 && !trustManager.isTrusted(owner, p) && !p.equals(owner)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 1, false, false));
                    }
                    if (tier >= 3 && !trustManager.isTrusted(owner, p) && !p.equals(owner)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration, 1, false, false));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (p.getLocation().distance(center) <= radius && p.isOnline()) {
                                    p.teleport(p.getLocation().add(0, 0.1, 0));
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 1L);
                    }
                }
            }
        }
    }

    private void triggerBobAbility1(Player owner, int tier) {
        int stunDuration = configManager.getConfig().getInt("cats.bob.tier1.ability1.duration", 2) * 20;
        int interval = configManager.getConfig().getInt("cats.bob.tier1.ability1.interval", 5) * 20;
        int totalDuration = configManager.getConfig().getInt("cats.bob.tier1.ability1.total-duration", 20) * 20;
        
        new BukkitRunnable() {
            int elapsed = 0;
            @Override
            public void run() {
                if (!owner.isOnline() || elapsed >= totalDuration) {
                    cancel();
                    return;
                }
                
                for (Entity entity : owner.getNearbyEntities(5, 5, 5)) {
                    if (entity instanceof LivingEntity target && target != owner) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunDuration, 255, false, false));
                        target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0);
                    }
                }
                
                if (tier >= 2) {
                    // Armor damage handled in listener
                }
                
                elapsed += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    private void triggerBobAbility2(Player owner, int tier) {
        int duration = configManager.getConfig().getInt("cats.bob.tier1.ability2.duration", 6) * 20;
        
        invisPlayers.add(owner.getUniqueId());
        owner.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
        
        if (tier >= 2) {
            // Can hit while invisible - handled in listener
        }
        
        owner.getWorld().spawnParticle(Particle.CLOUD, owner.getLocation(), 30, 0.5, 1, 0.5, 0.1);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.5f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                invisPlayers.remove(owner.getUniqueId());
                if (tier >= 3 && owner.getHealth() < 10) { // Under 5 hearts
                    owner.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 2, false, false)); // Strength 3 for 10 seconds
                }
            }
        }.runTaskLater(plugin, duration);
    }

    private void triggerLarryAbility1(Player owner, int tier) {
        int duration = configManager.getConfig().getInt("cats.larry.tier1.ability1.duration", 10) * 20;
        // Handled in listener when hitting
    }

    private void triggerLarryAbility2(Player owner, int tier) {
        int duration = configManager.getConfig().getInt("cats.larry.tier1.ability2.duration", 15) * 20;
        double reduction = configManager.getConfig().getDouble("cats.larry.tier1.ability2.damage-reduction", 0.75);
        
        owner.getWorld().spawnParticle(Particle.SHIELD, owner.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        owner.getWorld().playSound(owner.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
        
        // Handled in listener
    }

    public void onHitByCat(Player owner, LivingEntity target, CatData catData) {
        CatType type = catData.getCatType();
        int tier = catData.getTier();
        
        switch (type) {
            case LARRY -> {
                if (tier >= 1) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, false)); // 10 seconds
                }
                if (tier >= 3) {
                    for (PotionEffect effect : target.getActivePotionEffects()) {
                        target.removePotionEffect(effect.getType());
                    }
                }
                if (tier >= 3) {
                    // Tornado
                    Location loc = owner.getLocation();
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if (!owner.isOnline() || ticks > 100) { cancel(); return; }
                            for (Entity e : loc.getWorld().getNearbyEntities(loc, 5, 10, 5)) {
                                if (e instanceof LivingEntity le && !e.equals(owner)) {
                                    Vector v = e.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(0.5);
                                    v.setY(1.5);
                                    e.setVelocity(v);
                                }
                            }
                            loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().add(0, ticks * 0.1, 0), 10, 2, 0.5, 2, 0.1);
                            ticks++;
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }
            case BOB -> {
                if (tier >= 2) {
                    // Extra armor damage handled in listener
                }
            }
        }
    }

    public void onHitByOwner(Player owner, EntityDamageByEntityEvent event, CatData catData) {
        CatType type = catData.getCatType();
        int tier = catData.getTier();
        
        if (type == CatType.JAMES && tier >= 3 && owner.getHealth() < 8) { // Under 4 hearts
            event.setDamage(event.getDamage() + 2);
        }
    }

    public void onOwnerDamaged(EntityDamageEvent event, CatData catData) {
        if (immunePlayers.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    public void onOwnerAttack(EntityDamageByEntityEvent event, CatData catData) {
        if (invisPlayers.contains(event.getDamager().getUniqueId())) {
            if (catData.getCatType() == CatType.BOB && catData.getTier() < 2) {
                event.setCancelled(true);
            }
        }
    }

    public void onTargetDamaged(EntityDamageByEntityEvent event, CatData catData) {
        if (catData.getCatType() == CatType.LARRY && catData.getTier() >= 2) {
            // Speed boost for Larry tier 2 ability 2
            Player
        }
    }

    public boolean isImmune(Player player) {
        return immunePlayers.contains(player.getUniqueId());
    }

    public boolean isInvisible(Player player) {
        return invisPlayers.contains(player.getUniqueId());
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }
}