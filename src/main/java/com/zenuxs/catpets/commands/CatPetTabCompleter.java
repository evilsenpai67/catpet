package com.zenuxs.catpets.commands;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CatPetTabCompleter implements TabCompleter {
    private final CatPets plugin;

    public CatPetTabCompleter(CatPets plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "info", "tier", "trust", "untrust", "list", "ability1", "ability2");
            if (sender.hasPermission("catpets.admin")) {
                subCommands = new ArrayList<>(subCommands);
                subCommands.addAll(Arrays.asList("spawn", "despawn"));
            }
            return subCommands.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("trust") || subCommand.equals("untrust")) {
                return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
            }
            if (subCommand.equals("spawn") && sender.hasPermission("catpets.admin")) {
                return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.startsWith(args[1]))
                    .collect(Collectors.toList());
            }
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("spawn") && sender.hasPermission("catpets.admin")) {
            return Arrays.stream(CatType.values())
                .map(CatType::name)
                .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        return completions;
    }
}