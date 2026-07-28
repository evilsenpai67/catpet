package com.zenuxs.catpets.commands;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import com.zenuxs.catpets.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnTier3Command implements CommandExecutor {
    private final CatPets plugin;
    private final MessageManager messageManager;

    public SpawnTier3Command(CatPets plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("catpets.admin")) {
            messageManager.sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 1) {
            messageManager.sendPrefixedMessage(sender, "&cUsage: /spawntier3 <player> [catType]");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            messageManager.sendMessage(sender, "player-not-found");
            return true;
        }

        CatData catData = plugin.getCatManager().getCatData(target);
        if (catData == null) {
            messageManager.sendPrefixedMessage(sender, "&cThat player doesn't have a cat!");
            return true;
        }

        CatType catType = args.length >= 2 ? CatType.fromString(args[1]) : catData.getCatType();
        if (catType == null) catType = catData.getCatType();

        if (catData.getTier() >= 3) {
            messageManager.sendMessage(sender, "max-tier");
            return true;
        }

        if (plugin.getRitualManager().hasActiveRitual(target.getUniqueId())) {
            messageManager.sendPrefixedMessage(sender, "&cThat player already has an active ritual!");
            return true;
        }

        plugin.getRitualManager().startRitual(target, catType);
        return true;
    }
}