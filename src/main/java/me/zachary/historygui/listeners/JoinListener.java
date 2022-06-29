package me.zachary.historygui.listeners;

import me.zachary.historygui.Historygui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
	private Historygui plugin;

	public JoinListener(Historygui plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPlayedBefore())
			return;

		if(plugin.getPlayerManager().getPlayers().containsKey(player.getName()))
			return;
		plugin.getPlayerManager().addPlayer(new me.zachary.historygui.player.Player(player.getName(), player.getUniqueId()));
	}
}
