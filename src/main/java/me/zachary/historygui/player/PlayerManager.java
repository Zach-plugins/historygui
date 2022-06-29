package me.zachary.historygui.player;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.guis.HistoryGui;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private final Historygui plugin;
    private HashMap<String, Player> players = new HashMap<>();

    public PlayerManager(Historygui plugin) {
        this.plugin = plugin;

        load();
    }

    private void load() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "SELECT * FROM {history} WHERE uuid != 'CONSOLE' GROUP BY name";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                try (ResultSet rs = st.executeQuery()) {
                    while(rs.next())
                        addPlayer(new Player(rs.getString("name"), UUID.fromString(rs.getString("uuid"))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addPlayer(Player player) {
        players.put(player.getPlayerName(), player);
    }

    public Player getPlayer(UUID uuid) {
        for(Player player : players.values())
            if(player.getUUID().equals(uuid))
                return player;
        return null;
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }
}
