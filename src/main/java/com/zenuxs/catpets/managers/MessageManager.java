package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;

public class MessageManager {
    private final CatPets plugin;
    private ConfigManager configManager;

    public MessageManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
    }

    public void sendMessage(org.bukkit.command.CommandSender sender, String path, String... placeholders) {
        sender.sendMessage(configManager.getMessage(path, placeholders));
    }

    public void sendPrefixedMessage(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(configManager.getPrefix() + message);
    }

    public void broadcast(String path, String... placeholders) {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            sendMessage(player, path, placeholders);
        }
    }

    public String format(String path, String... placeholders) {
        return configManager.getMessage(path, placeholders);
    }

    public String getPrefix() {
        return configManager.getPrefix();
    }
}