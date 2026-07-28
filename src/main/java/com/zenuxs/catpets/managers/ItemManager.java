package com.zenuxs.catpets.managers;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private final CatPets plugin;
    private ConfigManager configManager;
    private NamespacedKey upgradeFishKey;
    private NamespacedKey tierKey;
    private NamespacedKey catTypeKey;
    private NamespacedKey ritualCoreKey;

    public ItemManager(CatPets plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.upgradeFishKey = new NamespacedKey(plugin, "upgrade_fish");
        this.tierKey = new NamespacedKey(plugin, "tier");
        this.catTypeKey = new NamespacedKey(plugin, "cat_type");
        this.ritualCoreKey = new NamespacedKey(plugin, "ritual_core");
    }

    public void reload() {
        this.configManager = plugin.getConfigManager();
    }

    public ItemStack createUpgradeFish(CatType catType, int tier) {
        Material material = Material.valueOf(configManager.getConfig().getString("upgrade-fish.material", "TROPICAL_FISH"));
        int customModelData = configManager.getConfig().getInt("upgrade-fish.custom-model-data", 1001);
        String name = configManager.getConfig().getString("upgrade-fish.name", "&d&lUpgrade Fish");
        List<String> lore = configManager.getConfig().getStringList("upgrade-fish.lore");

        ItemStack fish = new ItemStack(material);
        ItemMeta meta = fish.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(color(line.replace("%tier%", String.valueOf(tier))));
            }
            meta.setLore(coloredLore);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            meta.getPersistentDataContainer().set(upgradeFishKey, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(tierKey, PersistentDataType.INTEGER, tier);
            meta.getPersistentDataContainer().set(catTypeKey, PersistentDataType.STRING, catType.name().toLowerCase());
            fish.setItemMeta(meta);
        }
        return fish;
    }

    public ItemStack createRitualCore(CatType catType) {
        ItemStack core = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = core.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color("&6&lTier 3 Core &7- &d" + catType.getDisplayName()));
            List<String> lore = new ArrayList<>();
            lore.add(color("&7Use this to upgrade your cat to Tier 3!"));
            lore.add(color("&7Cat Type: &d" + catType.getDisplayName()));
            meta.setLore(lore);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.getPersistentDataContainer().set(ritualCoreKey, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(catTypeKey, PersistentDataType.STRING, catType.name().toLowerCase());
            core.setItemMeta(meta);
        }
        return core;
    }

    public boolean isUpgradeFish(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(upgradeFishKey, PersistentDataType.BYTE);
    }

    public boolean isRitualCore(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(ritualCoreKey, PersistentDataType.BYTE);
    }

    public int getUpgradeFishTier(ItemStack item) {
        if (!isUpgradeFish(item)) return 0;
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(tierKey, PersistentDataType.INTEGER, 0);
    }

    public CatType getUpgradeFishCatType(ItemStack item) {
        if (!isUpgradeFish(item)) return null;
        String typeStr = item.getItemMeta().getPersistentDataContainer().get(catTypeKey, PersistentDataType.STRING);
        if (typeStr == null) return null;
        try {
            return CatType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public CatType getRitualCoreCatType(ItemStack item) {
        if (!isRitualCore(item)) return null;
        String typeStr = item.getItemMeta().getPersistentDataContainer().get(catTypeKey, PersistentDataType.STRING);
        if (typeStr == null) return null;
        try {
            return CatType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String color(String s) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
    }
}