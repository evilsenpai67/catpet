package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrustManager {
    private final CatPets plugin;
    private ConfigManager configManager;
    private NamespacedKey trustedKey;
    private final java.util.Map<UUID, Set<UUID>> trustedPlayers = new java.util.concurrent.ConcurrentHashMap<>();

    public TrustManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.trustedKey = new NamespacedKey(plugin, "trusted_players");
        loadAllTrusted();
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
    }

    private void loadAllTrusted() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            loadTrusted(player);
        }
    }

    public void loadTrusted(Player player) {
        if (player.getPersistentDataContainer().has(trustedKey, PersistentDataType.TAG_CONTAINER_ARRAY)) {
            org.bukkit.persistence.PersistentDataContainer container = player.getPersistentDataContainer();
            org.bukkit.persistence.PersistentDataContainer[] arrays = container.get(trustedKey, PersistentDataType.TAG_CONTAINER_ARRAY);
            if (arrays != null) {
                Set<UUID> trusted = new HashSet<>();
                for (org.bukkit.persistence.PersistentDataContainer arr : arrays) {
                    String uuidStr = arr.get(new NamespacedKey(plugin, "uuid"), PersistentDataType.STRING);
                    if (uuidStr != null) {
                        trusted.add(UUID.fromString(uuidStr));
                    }
                }
                trustedPlayers.put(player.getUniqueId(), trusted);
            }
        }
    }

    public void saveTrusted(Player player) {
        Set<UUID> trusted = trustedPlayers.get(player.getUniqueId());
        if (trusted == null || trusted.isEmpty()) {
            player.getPersistentDataContainer().remove(trustedKey);
            return;
        }
        org.bukkit.persistence.PersistentDataContainer[] arrays = new org.bukkit.persistence.PersistentDataContainer[trusted.size()];
        int i = 0;
        for (UUID uuid : trusted) {
            org.bukkit.persistence.PersistentDataContainer container = plugin.getServer().createPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "uuid"), PersistentDataType.STRING, uuid.toString());
            arrays[i++] = container;
        }
        player.getPersistentDataContainer().set(trustedKey, PersistentDataType.TAG_CONTAINER_ARRAY, arrays);
    }

    public boolean trust(Player owner, Player target) {
        if (owner.equals(target)) return false;
        Set<UUID> trusted = trustedPlayers.computeIfAbsent(owner.getUniqueId(), k -> new HashSet<>());
        int maxTrusted = configManager.getMaxTrustedPlayers();
        if (trusted.size() >= maxTrusted && !trusted.contains(target.getUniqueId())) {
            return false;
        }
        return trusted.add(target.getUniqueId());
    }

    public boolean untrust(Player owner, Player target) {
        Set<UUID> trusted = trustedPlayers.get(owner.getUniqueId());
        if (trusted == null) return false;
        return trusted.remove(target.getUniqueId());
    }

    public boolean isTrusted(Player owner, Player target) {
        if (owner.equals(target)) return true;
        Set<UUID> trusted = trustedPlayers.get(owner.getUniqueId());
        return trusted != null && trusted.contains(target.getUniqueId());
    }

    public Set<UUID> getTrusted(Player owner) {
        return new HashSet<>(trustedPlayers.getOrDefault(owner.getUniqueId(), new HashSet<>()));
    }

    public String getTrustedList(Player owner) {
        Set<UUID> trusted = getTrusted(owner);
        if (trusted.isEmpty()) return "";
        return trusted.stream()
            .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.joining(", "));
    }

    public void clearTrusted(Player owner) {
        trustedPlayers.remove(owner.getUniqueId());
        owner.getPersistentDataContainer().remove(trustedKey);
    }
}