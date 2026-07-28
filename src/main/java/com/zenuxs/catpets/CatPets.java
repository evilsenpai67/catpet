package com.zenuxs.catpets;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public class CatPets extends JavaPlugin {

    private static CatPets instance;
    private FileConfiguration config;
    private File configFile;
    private CatManager catManager;
    private AbilityManager abilityManager;
    private RitualManager ritualManager;
    private TrustManager trustManager;
    private ItemManager itemManager;
    private MessageManager messageManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfig();
        
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.itemManager = new ItemManager(this);
        this.trustManager = new TrustManager(this);
        this.abilityManager = new AbilityManager(this);
        this.catManager = new CatManager(this);
        this.ritualManager = new RitualManager(this);

        registerCommands();
        registerListeners();
        registerPlaceholderAPI();

        getLogger().info("CatPets enabled successfully!");
        
        if (config.getBoolean("update-checker.enabled", true)) {
            checkForUpdates();
        }
    }

    @Override
    public void onDisable() {
        catManager.despawnAllCats();
        ritualManager.cancelAllRituals();
        getLogger().info("CatPets disabled.");
    }

    private void loadConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void registerCommands() {
        getCommand("catpet").setExecutor(new CatPetCommand(this));
        getCommand("catpet").setTabCompleter(new CatPetTabCompleter(this));
        getCommand("spawntier3").setExecutor(new SpawnTier3Command(this));
        getCommand("giveupgradefish").setExecutor(new GiveUpgradeFishCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new CatListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new RitualListener(this), this);
        getServer().getPluginManager().registerEvents(new TrustListener(this), this);
    }

    private void registerPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new CatPetsExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }
    }

    private void checkForUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String repo = config.getString("update-checker.repo", "Zenuxs/CatPets");
                    String url = "https://api.github.com/repos/" + repo + "/releases/latest";
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                    conn.setRequestProperty("User-Agent", "CatPets-Updater");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    
                    if (conn.getResponseCode() == 200) {
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                                new java.io.InputStreamReader(conn.getInputStream()))) {
                            StringBuilder json = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) json.append(line);
                            
                            String latestVersion = extractJsonValue(json.toString(), "tag_name");
                            if (latestVersion != null && !latestVersion.equals(getDescription().getVersion())) {
                                getLogger().warning("Update available: " + latestVersion + " (current: " + getDescription().getVersion() + ")");
                                getLogger().warning("Download at: https://github.com/" + repo + "/releases/latest");
                            }
                        }
                    }
                } catch (Exception e) {
                    if (config.getBoolean("general.debug", false)) {
                        getLogger().log(Level.WARNING, "Update check failed", e);
                    }
                }
            }
        }.runTaskAsynchronously(this);
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;
        idx += search.length();
        int end = json.indexOf("\"", idx);
        return json.substring(idx, end);
    }

    public static CatPets getInstance() {
        return instance;
    }

    public FileConfiguration getConfigFile() {
        return config;
    }

    public void reloadConfigFile() {
        config = YamlConfiguration.loadConfiguration(configFile);
        configManager.reload();
        messageManager.reload();
        itemManager.reload();
        catManager.reload();
        abilityManager.reload();
        ritualManager.reload();
        trustManager.reload();
    }

    public void saveConfigFile() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config", e);
        }
    }

    public CatManager getCatManager() { return catManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public RitualManager getRitualManager() { return ritualManager; }
    public TrustManager getTrustManager() { return trustManager; }
    public ItemManager getItemManager() { return itemManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public ConfigManager getConfigManager() { return configManager; }
}