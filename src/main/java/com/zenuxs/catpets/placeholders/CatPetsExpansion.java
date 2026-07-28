package com.zenuxs.catpets.expansion;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CatPetsExpansion extends PlaceholderExpansion {
    private final CatPets plugin;

    public CatPetsExpansion(CatPets plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "catpets";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zenuxs";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            return switch (params.toLowerCase()) {
                case "hascat" -> "false";
                case "catname", "cattype", "cattier" -> "None";
                default -> null;
            };
        }

        return switch (params.toLowerCase()) {
            case "hascat" -> "true";
            case "catname" -> catData.getCatType().getDisplayName();
            case "cattype" -> catData.getCatType().name().toLowerCase();
            case "cattier" -> String.valueOf(catData.getTier());
            case "catdisplay" -> catData.getCatType().getDisplayName() + " &7(Tier " + catData.getTier() + ")";
            default -> null;
        };
    }
}