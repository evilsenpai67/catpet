package com.zenuxs.catpets.commands;

import com.zenuxs.catpets.CatPets;
import com.zenuxs.catpets.data.CatData;
import com.zenuxs.catpets.data.CatType;
import com.zenuxs.catpets.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveUpgradeFishCommand implements CommandExecutor {
    private final CatPets plugin;
    private final MessageManager messageManager;

    public GiveUpgradeFishCommand(CatPets plugin) {
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
            messageManager.sendPrefixedMessage(sender, "&cUsage: /giveupgradefish <player> [catType]");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            messageManager.sendMessage(sender, "player-not-found");
            return true;
        }

        CatData catData = plugin.getCatManager().getCatData(target);
        CatType catType = args.length >= 2 ? CatType.fromString(args[1]) : (catData != null ? catData.getCatType() : CatType.getRandom());
        if (catType == null) catType = CatType.getRandom();

        int tier = catData != null ? catData.getTier() + 1 : 2;
        if (tier > 3) tier = 3;

        target.getInventory().addItem(plugin.getItemManager().createUpgradeFish(catType, tier));
        messageManager.sendPrefixedMessage(sender, "&aGave &d" + catType.getDisplayName() + " &aUpgrade Fish (Tier " + tier + ") to &e" + target.getName());
        return true;
    }
}