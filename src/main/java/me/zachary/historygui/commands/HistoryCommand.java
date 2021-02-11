package me.zachary.historygui.commands;

import me.zachary.historygui.Historygui;
import me.zachary.historygui.guis.BrowseGui;
import me.zachary.historygui.guis.HistoryGui;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
    private Historygui plugin;

    public HistoryCommand(Historygui plugin) {
        this.plugin = plugin;
        plugin.getCommand("check").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("You need be a player to execute this command.");
            return true;
        }
        Player player = (Player)sender;
        if(!player.hasPermission("history.gui")){
            MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("No permission"));
            return false;
        }

        if(args.length < 1){
            new BrowseGui(plugin).openBrowseGui(player);
            return true;
        }

        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
        if(!target.hasPlayedBefore()){
            MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Player not found"));
            return true;
        }
        player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target));
        return false;
    }
}
