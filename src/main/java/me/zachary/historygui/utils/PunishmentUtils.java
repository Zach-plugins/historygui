package me.zachary.historygui.utils;

import com.zachfr.zachlibrary.utils.ChatPromptUtils;
import com.zachfr.zachlibrary.utils.LogUtils;
import litebans.api.Database;
import me.zachary.historygui.Historygui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PunishmentUtils {

    public static void editPunishment(Historygui plugin, Player player, String tableName, int id){
        Bukkit.getScheduler().runTask(plugin, () -> {
            ChatPromptUtils.showPrompt(plugin, player, plugin.getLanguage().getMessage("Enter new reason of punishment").getMessage(), chatConfirmEvent -> {
                String query = "UPDATE "+tableName+" SET `reason`='"+chatConfirmEvent.getMessage()+"' WHERE id = "+id;
                try (PreparedStatement st = Database.get().prepareStatement(query)) {
                    st.executeUpdate();
                    plugin.getLanguage().getMessage("Successfull edit punishment").processPlaceholder("reason", chatConfirmEvent.getMessage()).sendMessage(player);
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
                plugin.getLanguage().getMessage("Successfull delete punishment").sendMessage(player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void removePunishment(Historygui plugin, Player player, me.zachary.historygui.player.Player target, String tableName, int id) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            ChatPromptUtils.showPrompt(plugin, player, plugin.getLanguage().getMessage("Enter remove reason").getMessage(), chatConfirmEvent -> {
                // removed_by_uuid removed_by_name removed_by_reason removed_by_date active
                String query = "UPDATE "+tableName+" SET `removed_by_uuid`='"+player.getUniqueId()+"', `removed_by_name`='"+player.getName()+"', `removed_by_reason`='"+chatConfirmEvent.getMessage()+"', `removed_by_date`='"+new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date())+"', `active`=0 WHERE id = "+id;
                try (PreparedStatement st = Database.get().prepareStatement(query)) {
                    st.executeUpdate();
                    plugin.getLanguage().getMessage("Successfull remove punishment").processPlaceholder("reason", chatConfirmEvent.getMessage()).sendMessage(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
