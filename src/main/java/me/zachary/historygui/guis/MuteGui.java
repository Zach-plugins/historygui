package me.zachary.historygui.guis;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
import me.zachary.historygui.utils.LoreUtils;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.utils.TimerBuilder;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MuteGui {
    private Historygui plugin;
    private Boolean sort = false;

    public MuteGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openMuteInventory(Player player, OfflinePlayer target){
        ZMenu muteGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Mute.Title name").replace("{target}", target.getName()), plugin.getGuiConfig().getInt("Gui.Mute.Row") + 2);
        muteGUI.setAutomaticPaginationEnabled(true);
        muteGUI.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, target, () -> {
            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target));
        }, sort, inventoryClickEvent -> {
            sort = !sort;
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
                openMuteInventory(player, target);
            });
        }));
        GuiUtils.setGlass(muteGUI, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query;
            if(sort)
                query = "SELECT * FROM {mutes} WHERE uuid=? ORDER BY TIME ASC";
            else
                query = "SELECT * FROM {mutes} WHERE uuid=? ORDER BY TIME DESC";
            int slot = 10;
            int page = 0;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, String.valueOf(target.getUniqueId()));
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        List<String> replace = new ArrayList<>();
                        List<String> replacement = new ArrayList<>();
                        List<String> remove = new ArrayList<>();
                        List<String> duration = new ArrayList();
                        replace.add("{name}");
                        replacement.add(rs.getString("banned_by_name"));
                        replace.add("{server}");
                        replacement.add(rs.getString("server_origin") != null ? rs.getString("server_origin") : "Server");
                        replace.add("{reason}");
                        replacement.add(rs.getString("reason"));
                        replace.add("{remove}");
                        if(rs.getString("removed_by_name") == null)
                            remove.addAll(plugin.getGuiConfig().getStringList("Sanction remove.false"));
                        else{
                            List<String> rRemove = new ArrayList<>();
                            List<String> rrRemove = new ArrayList<>();
                            rRemove.add("{remover}");
                            rrRemove.add(rs.getString("removed_by_name"));
                            rRemove.add("{time}");
                            rrRemove.add(rs.getString("removed_by_date"));
                            remove.addAll(LoreUtils.getLore("Sanction remove.true", rRemove, rrRemove, null, null));
                        }
                        replace.add("{duration}");
                        if(rs.getLong("until") == -1){
                            duration.addAll(plugin.getGuiConfig().getStringList("Sanction duration.permanent"));
                        }else{
                            List<String> rDuration = new ArrayList<>();
                            List<String> rrDuration = new ArrayList<>();
                            rDuration.add("{time}");
                            rrDuration.add(TimerBuilder.getFormatLongDays(rs.getLong("until") - rs.getLong("time")));
                            rDuration.add("{expire}");
                            rrDuration.add(new Date(rs.getLong("until")).toString());
                            duration.addAll(LoreUtils.getLore("Sanction duration.not permanent", rDuration, rrDuration, null, null));
                        }
                        int id = rs.getInt("id");
                        String reason = rs.getString("reason");
                        ZButton banButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Mute.Icon")).parseItem())
                                .name(plugin.getGuiConfig().getString("Gui.Mute.Icon name").replace("{time}", new Date(rs.getLong("time")).toString()))
                                .lore(LoreUtils.getLore("Gui.Mute.Content", replace, replacement, remove, duration))
                                .build()).withListener(inventoryClickEvent -> {
                                    if(plugin.getConfig().getBoolean("Allow edit punishment") && player.hasPermission("historygui.edit.mute"))
                                        new EditGui(plugin).openEditGui(player, target, "{mutes}", reason, id);
                        });

                        muteGUI.setButton(page, slot, banButton);

                        if(plugin.getGuiConfig().getInt("Gui.Mute.Row") == 1){
                            slot++;
                            if(slot == 17){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(muteGUI, page);
                            }
                        } else if(plugin.getGuiConfig().getInt("Gui.Mute.Row") == 2){
                            if(slot == 16)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 26){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(muteGUI, page);
                            }
                        }else if(plugin.getGuiConfig().getInt("Gui.Mute.Row") == 3){
                            if(slot == 16 || slot == 25)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 35){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(muteGUI, page);
                            }
                        }else
                            slot++; // ?
                    }
                    if(muteGUI.getButton(10) == null){
                        ZButton emptyButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Empty punishment button.Item")).parseItem())
                                .name(plugin.getGuiConfig().getString("Empty punishment button.Name"))
                                .lore(LoreUtils.getLore("Empty punishment button.Lore", "{player}", target.getName(), "{category}", "mute"))
                                .build());
                        muteGUI.setButton(13, emptyButton);
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(muteGUI.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
