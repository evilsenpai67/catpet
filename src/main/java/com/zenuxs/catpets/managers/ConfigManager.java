package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final CatPets plugin;
    private FileConfiguration config;

    public ConfigManager(CatPets plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigFile();
        createDefaultConfig();
    }

    private void createDefaultConfig() {
        config.addDefault("general.debug", false);
        config.addDefault("general.cat-spawn-delay", 5);
        config.addDefault("general.max-trusted-players", 10);
        
        config.addDefault("cats.james.tier1.ability1.duration", 5);
        config.addDefault("cats.james.tier1.ability1.cooldown", 30);
        config.addDefault("cats.james.tier1.ability2.radius", 7);
        config.addDefault("cats.james.tier1.ability2.duration", 10);
        config.addDefault("cats.james.tier1.ability2.regen-level", 2);
        
        config.addDefault("cats.bob.tier1.ability1.duration", 2);
        config.addDefault("cats.bob.tier1.ability1.interval", 5);
        config.addDefault("cats.bob.tier1.ability1.total-duration", 20);
        config.addDefault("cats.bob.tier1.ability2.duration", 6);
        
        config.addDefault("cats.larry.tier1.ability1.duration", 10);
        config.addDefault("cats.larry.tier1.ability2.duration", 15);
        config.addDefault("cats.larry.tier1.ability2.damage-reduction", 0.75);
        
        config.addDefault("upgrade-fish.name", "&d&lUpgrade Fish");
        config.addDefault("upgrade-fish.lore", java.util.Arrays.asList("&7Feed this to your cat to upgrade it!", "&7Tier: &e%tier%"));
        config.addDefault("upgrade-fish.material", "TROPICAL_FISH");
        config.addDefault("upgrade-fish.custom-model-data", 1001);
        
        config.addDefault("ritual.duration", 3600);
        config.addDefault("ritual.particles.enabled", true);
        config.addDefault("ritual.particles.type", "ENCHANTMENT_TABLE");
        config.addDefault("ritual.particles.count", 20);
        config.addDefault("ritual.particles.radius", 3);
        config.addDefault("ritual.taskbar.enabled", true);
        config.addDefault("ritual.taskbar.title", "&6&lTier 3 Ritual in Progress");
        config.addDefault("ritual.sounds.enabled", true);
        config.addDefault("ritual.sounds.interval", 300);
        
        config.addDefault("messages.prefix", "&8[&dCatPets&8] &r");
        config.addDefault("messages.no-permission", "&cYou don't have permission to use this command!");
        config.addDefault("messages.player-only", "&cThis command can only be used by players!");
        config.addDefault("messages.player-not-found", "&cPlayer not found!");
        config.addDefault("messages.already-has-cat", "&cYou already have a cat pet!");
        config.addDefault("messages.cat-spawned", "&aYour cat %cat% has spawned!");
        config.addDefault("messages.cat-despawned", "&cYour cat has been despawned.");
        config.addDefault("messages.cat-upgraded", "&aYour cat %cat% has been upgraded to Tier %tier%!");
        config.addDefault("messages.max-tier", "&cYour cat is already at max tier!");
        config.addDefault("messages.ritual-started", "&6&lRitual started for %player%'s %cat%!");
        config.addDefault("messages.ritual-completed", "&6&lRitual completed! %player% received Tier 3 Core for %cat%!");
        config.addDefault("messages.ritual-cancelled", "&cRitual cancelled!");
        config.addDefault("messages.trust-added", "&aYou have trusted %player%!");
        config.addDefault("messages.trust-removed", "&cYou have untrusted %player%!");
        config.addDefault("messages.already-trusted", "&c%player% is already trusted!");
        config.addDefault("messages.not-trusted", "&c%player% is not trusted!");
        config.addDefault("messages.trust-list", "&eTrusted players: %list%");
        config.addDefault("messages.no-trusted", "&cYou have no trusted players.");
        config.addDefault("messages.cat-info", "&e=== Cat Info ===\n&7Name: &f%cat%\n&7Tier: &f%tier%\n&7Owner: &f%owner%");
        
        config.addDefault("update-checker.enabled", true);
        config.addDefault("update-checker.repo", "Zenuxs/CatPets");
        
        config.options().copyDefaults(true);
        plugin.saveConfigFile();
    }

    public void reload() {
        plugin.reloadConfigFile();
        this.config = plugin.getConfigFile();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isDebug() {
        return config.getBoolean("general.debug", false);
    }

    public int getCatSpawnDelay() {
        return config.getInt("general.cat-spawn-delay", 5);
    }

    public int getMaxTrustedPlayers() {
        return config.getInt("general.max-trusted-players", 10);
    }

    public String getPrefix() {
        return color(config.getString("messages.prefix", "&8[&dCatPets&8] &r"));
    }

    public String getMessage(String path) {
        return color(config.getString("messages." + path, "&cMessage not found: " + path));
    }

    public String getMessage(String path, String... placeholders) {
        String msg = getMessage(path);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                msg = msg.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
            }
        }
        return msg;
    }

    private String color(String s) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }
}