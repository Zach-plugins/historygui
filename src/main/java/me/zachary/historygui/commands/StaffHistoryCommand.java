package me.zachary.historygui.commands;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.guis.StaffHistoryGui;
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
			plugin.getLanguage().getMessage("No permission").sendMessage(player);
			return false;
		}

		if(args.length < 1){
			plugin.getLanguage().getMessage("Staff history command usage").sendMessage(player);
			return true;
		}

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String query = "SELECT * FROM {history} WHERE name = ? ORDER BY date DESC";
			try (PreparedStatement st = Database.get().prepareStatement(query)){
				st.setString(1, args[0]);
				try (ResultSet rs = st.executeQuery()) {
					if(!rs.next()){
						plugin.getLanguage().getMessage("Player not found").sendMessage(player);
						return;
					}
					plugin.getLanguage().getMessage("Loading staff history").processPlaceholder("target", args[0]).sendMessage(player);
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
