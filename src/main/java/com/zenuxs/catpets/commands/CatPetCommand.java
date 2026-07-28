package com.zenuxs.catpets.commands;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import com.zenuxs.catpets.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CatPetCommand implements CommandExecutor {
    private final CatPets plugin;
    private final MessageManager messageManager;

    public CatPetCommand(CatPets plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messageManager.sendMessage(sender, "player-only");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help" -> sendHelp(player);
            case "info" -> showInfo(player);
            case "tier" -> showTier(player);
            case "trust" -> trustPlayer(player, args);
            case "untrust" -> untrustPlayer(player, args);
            case "list" -> listTrusted(player);
            case "spawn" -> spawnCat(player, args);
            case "despawn" -> despawnCat(player);
            case "ability1" -> useAbility1(player);
            case "ability2" -> useAbility2(player);
            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        messageManager.sendPrefixedMessage(player, "&e=== &dCatPets Help &e===");
        messageManager.sendPrefixedMessage(player, "&7/catpet help &8- &fShow this help");
        messageManager.sendPrefixedMessage(player, "&7/catpet info &8- &fShow your cat info");
        messageManager.sendPrefixedMessage(player, "&7/catpet tier &8- &fShow your cat's tier");
        messageManager.sendPrefixedMessage(player, "&7/catpet trust <player> &8- &fTrust a player");
        messageManager.sendPrefixedMessage(player, "&7/catpet untrust <player> &8- &fUntrust a player");
        messageManager.sendPrefixedMessage(player, "&7/catpet list &8- &fList trusted players");
        if (player.hasPermission("catpets.admin")) {
            messageManager.sendPrefixedMessage(player, "&7/catpet spawn <player> [type] &8- &cAdmin: Spawn cat for player");
            messageManager.sendPrefixedMessage(player, "&7/catpet despawn &8- &cAdmin: Despawn your cat");
        }
    }

    private void showInfo(Player player) {
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            messageManager.sendMessage(player, "no-cat");
            return;
        }
        messageManager.sendPrefixedMessage(player, messageManager.format("cat-info",
            "cat", catData.getCatType().getDisplayName(),
            "tier", String.valueOf(catData.getTier()),
            "owner", player.getName()));
    }

    private void showTier(Player player) {
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            messageManager.sendMessage(player, "no-cat");
            return;
        }
        messageManager.sendPrefixedMessage(player, "&eYour cat &d" + catData.getCatType().getDisplayName() + " &eis at &6Tier " + catData.getTier());
    }

    private void trustPlayer(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.sendPrefixedMessage(player, "&cUsage: /catpet trust <player>");
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            messageManager.sendMessage(player, "player-not-found");
            return;
        }
        if (target.equals(player)) {
            messageManager.sendPrefixedMessage(player, "&cYou cannot trust yourself!");
            return;
        }
        if (plugin.getTrustManager().trust(player, target)) {
            messageManager.sendMessage(player, "trust-added", "player", target.getName());
        } else {
            messageManager.sendMessage(player, "already-trusted", "player", target.getName());
        }
    }

    private void untrustPlayer(Player player, String[] args) {
        if (args.length < 2) {
            messageManager.sendPrefixedMessage(player, "&cUsage: /catpet untrust <player>");
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            messageManager.sendMessage(player, "player-not-found");
            return;
        }
        if (plugin.getTrustManager().untrust(player, target)) {
            messageManager.sendMessage(player, "trust-removed", "player", target.getName());
        } else {
            messageManager.sendMessage(player, "not-trusted", "player", target.getName());
        }
    }

    private void listTrusted(Player player) {
        String list = plugin.getTrustManager().getTrustedList(player);
        if (list.isEmpty()) {
            messageManager.sendMessage(player, "no-trusted");
        } else {
            messageManager.sendPrefixedMessage(player, messageManager.format("trust-list", "list", list));
        }
    }

    private void spawnCat(Player player, String[] args) {
        if (!player.hasPermission("catpets.admin")) {
            messageManager.sendMessage(player, "no-permission");
            return;
        }
        Player target = args.length >= 2 ? plugin.getServer().getPlayer(args[1]) : player;
        if (target == null) {
            messageManager.sendMessage(player, "player-not-found");
            return;
        }
        CatType type = args.length >= 3 ? CatType.fromString(args[2]) : CatType.getRandom();
        if (type == null) type = CatType.getRandom();
        
        CatData existing = plugin.getCatManager().getCatData(target);
        if (existing != null) {
            messageManager.sendMessage(player, "already-has-cat");
            return;
        }
        plugin.getCatManager().spawnCat(target);
    }

    private void despawnCat(Player player) {
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            messageManager.sendPrefixedMessage(player, "&cYou don't have a cat!");
            return;
        }
        plugin.getCatManager().despawnCat(player);
        messageManager.sendMessage(player, "cat-despawned");
    }

    private void useAbility1(Player player) {
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            messageManager.sendPrefixedMessage(player, "&cYou don't have a cat!");
            return;
        }
        plugin.getAbilityManager().triggerAbility1(catData);
    }

    private void useAbility2(Player player) {
        CatData catData = plugin.getCatManager().getCatData(player);
        if (catData == null) {
            messageManager.sendPrefixedMessage(player, "&cYou don't have a cat!");
            return;
        }
        plugin.getAbilityManager().triggerAbility2(catData);
    }
}