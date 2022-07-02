package me.zachary.historygui.commands;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.guis.StaffHistoryGui;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String query = "SELECT * FROM {history} WHERE name = ? ORDER BY date DESC";
			try (PreparedStatement st = Database.get().prepareStatement(query)){
				st.setString(1, args[0]);
				try (ResultSet rs = st.executeQuery()) {
					if(!rs.next()){
						MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Player not found"));
						return;
					}
					MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Loading staff history").replace("{target}", args[0]));
					UUID uuid = UUID.fromString(rs.getString("uuid"));
					new StaffHistoryGui(plugin, player, uuid, args[0]).getInventory();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		return false;
	}
}
