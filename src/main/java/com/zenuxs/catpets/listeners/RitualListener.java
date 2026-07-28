package com.zenuxs.catpets.listeners;

import com.zenuxs.catpets.CatPets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RitualListener implements Listener {
    private final CatPets plugin;

    public RitualListener(CatPets plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getRitualManager().hasActiveRitual(event.getPlayer().getUniqueId())) {
            plugin.getRitualManager().cancelRitual(event.getPlayer().getUniqueId());
        }
    }
}