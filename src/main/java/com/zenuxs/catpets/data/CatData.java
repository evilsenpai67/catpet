package com.zenuxs.catpets.data;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;

public class CatData {
    private final UUID ownerId;
    private final CatType catType;
    private int tier;
    private Cat catEntity;
    private long lastAbility1Use;
    private long lastAbility2Use;

    public CatData(UUID ownerId, CatType catType) {
        this.ownerId = ownerId;
        this.catType = catType;
        this.tier = 1;
    }

    public UUID getOwnerId() { return ownerId; }
    public CatType getCatType() { return catType; }
    public int getTier() { return tier; }
    public void setTier(int tier) { this.tier = Math.min(3, Math.max(1, tier)); }
    public Cat getCatEntity() { return catEntity; }
    public void setCatEntity(Cat catEntity) { this.catEntity = catEntity; }
    public long getLastAbility1Use() { return lastAbility1Use; }
    public void setLastAbility1Use(long time) { this.lastAbility1Use = time; }
    public long getLastAbility2Use() { return lastAbility2Use; }
    public void setLastAbility2Use(long time) { this.lastAbility2Use = time; }

    public boolean isAbility1Ready(long cooldown) {
        return System.currentTimeMillis() - lastAbility1Use >= cooldown;
    }

    public boolean isAbility2Ready(long cooldown) {
        return System.currentTimeMillis() - lastAbility2Use >= cooldown;
    }

    public Player getOwner() {
        return org.bukkit.Bukkit.getPlayer(ownerId);
    }
}