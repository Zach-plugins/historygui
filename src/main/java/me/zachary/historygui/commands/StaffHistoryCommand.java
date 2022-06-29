package me.zachary.historygui.commands;

import me.zachary.historygui.Historygui;
import me.zachary.historygui.guis.StaffHistoryGui;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffHistoryCommand implements CommandExecutor {
	private Historygui plugin;

	public StaffHistoryCommand(Historygui plugin) {
		this.plugin = plugin;
		plugin.getCommand("staffhistory").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("You need be a player to execute this command.");
			return true;
		}
		Player player = (Player)sender;
		if(!player.hasPermission("historygui.staff.history")){
			MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("No permission"));
			return false;
		}

		if(args.length < 1){
			MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Staff history command usage"));
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		if(target.getName() == null){
			MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Player not found"));
			return true;
		}

		player.openInventory(new StaffHistoryGui(plugin, player, target).getInventory());

		return false;
	}
}
