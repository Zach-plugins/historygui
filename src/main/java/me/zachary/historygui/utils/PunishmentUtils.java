package me.zachary.historygui.utils;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.zachcore.utils.ChatPromptUtils;
import me.zachary.zachcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PunishmentUtils {

    public static void editPunishment(Historygui plugin, Player player, String tableName, int id){
        Bukkit.getScheduler().runTask(plugin, () -> {
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

    public static void removePunishment(Historygui plugin, Player player, me.zachary.historygui.player.Player target, String tableName, int id) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            ChatPromptUtils.showPrompt(plugin, player, plugin.getMessageConfig().getString("Enter remove reason"), chatConfirmEvent -> {
                // removed_by_uuid removed_by_name removed_by_reason removed_by_date active
                String query = "UPDATE "+tableName+" SET `removed_by_uuid`='"+player.getUniqueId()+"', `removed_by_name`='"+player.getName()+"', `removed_by_reason`='"+chatConfirmEvent.getMessage()+"', `removed_by_date`='"+new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date())+"', `active`=0 WHERE id = "+id;
                try (PreparedStatement st = Database.get().prepareStatement(query)) {
                    st.executeUpdate();
                    MessageUtils.sendMessage(player, plugin.getMessageConfig().getString("Successfull remove punishment").replace("{reason}", chatConfirmEvent.getMessage()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
