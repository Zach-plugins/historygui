package me.zachary.historygui.utils;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.zachcore.utils.ChatPromptUtils;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PunishmentUtils {

    public static void editPunishment(Historygui plugin, Player player, String tableName, int id){
        System.out.println(plugin.getMessageConfig().getString("Enter new reason of punishment"));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ChatPromptUtils.showPrompt(plugin, player, plugin.getMessageConfig().getString("Enter new reason of punishment"), chatConfirmEvent -> {
                String query = "UPDATE "+tableName+" SET `reason`='"+chatConfirmEvent.getMessage()+"' WHERE id = "+id;
                try (PreparedStatement st = Database.get().prepareStatement(query)) {
                    st.executeUpdate();
                    MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Successfull edit punishment").replace("{reason}", chatConfirmEvent.getMessage()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static void deletePunishment(Historygui plugin, Player player, String tableName, int id){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "delete from "+tableName+" WHERE id = "+id;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.executeUpdate();
                MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Successfull delete punishment"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
