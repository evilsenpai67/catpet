package com.zenuxs.catpets.listeners;

import com.zenuxs.catpets.CatPets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TrustListener implements Listener {
    private final CatPets plugin;

    public TrustListener(CatPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTrustManager().loadTrusted(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getTrustManager().saveTrusted(event.getPlayer());
    }
}