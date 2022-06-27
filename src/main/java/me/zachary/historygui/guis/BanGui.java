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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BanGui {
    private Historygui plugin;
    private Boolean sort = false;

    public BanGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openBanInventory(Player player, me.zachary.historygui.player.Player target) {
        openBanInventory(player, target, false);
    }

    public void openBanInventory(Player player, me.zachary.historygui.player.Player target, Boolean fromCommand){
        ZMenu banGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Ban.Title name").replace("{target}", target.getPlayerName()), plugin.getGuiConfig().getInt("Gui.Ban.Row") + 2);
        banGUI.setAutomaticPaginationEnabled(true);
        banGUI.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, target, () -> {
            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target, fromCommand));
        }, sort, inventoryClickEvent -> {
            sort = !sort;
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
                openBanInventory(player, target);
            });
        }));
        GuiUtils.setGlass(banGUI, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query;
            if(sort)
                query = "SELECT * FROM {bans} WHERE uuid=? ORDER BY TIME ASC";
            else
                query = "SELECT * FROM {bans} WHERE uuid=? ORDER BY TIME DESC";
            int slot = 10;
            int page = 0;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, String.valueOf(target.getUUID()));
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
                        if(rs.getLong("until") == -1 || rs.getLong("until") == 0){
                            duration.addAll(plugin.getGuiConfig().getStringList("Sanction duration.permanent"));
                        }else{
                            List<String> rDuration = new ArrayList<>();
                            List<String> rrDuration = new ArrayList<>();
                            rDuration.add("{time}");
                            rrDuration.add(GuiUtils.getDuration(rs.getLong("until") - rs.getLong("time")));
                            rDuration.add("{expire}");
                            rrDuration.add(new Date(rs.getLong("until")).toString());
                            duration.addAll(LoreUtils.getLore("Sanction duration.not permanent", rDuration, rrDuration, null, null));
                        }

                        String reason = rs.getString("reason");
                        int id = rs.getInt("id");
                        ZButton banButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Ban.Icon")).parseItem())
                        .name(plugin.getGuiConfig().getString("Gui.Ban.Icon name").replace("{time}", new SimpleDateFormat(plugin.getGuiConfig().getString("Date fomat")).format(new Date(rs.getLong("time")))))
                        .lore(LoreUtils.getLore("Gui.Ban.Content", replace, replacement, remove, duration))
                        .build()).withListener(inventoryClickEvent -> {
                            if(plugin.getConfig().getBoolean("Allow edit punishment") && player.hasPermission("historygui.edit.ban"))
                                new EditGui(plugin).openEditGui(player, target, "{bans}", reason, id);
                        });

                        banGUI.setButton(page, slot, banButton);

                        if(plugin.getGuiConfig().getInt("Gui.Ban.Row") == 1){
                            slot++;
                            if(slot == 17){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(banGUI, page);
                            }
                        } else if(plugin.getGuiConfig().getInt("Gui.Ban.Row") == 2){
                            if(slot == 16)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 26){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(banGUI, page);
                            }
                        }else if(plugin.getGuiConfig().getInt("Gui.Ban.Row") == 3){
                            if(slot == 16 || slot == 25)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 35){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(banGUI, page);
                            }
                        }else
                            slot++; // ?
                    }
                    if(banGUI.getButton(10) == null){
                        ZButton emptyButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Empty punishment button.Item")).parseItem())
                                .name(plugin.getGuiConfig().getString("Empty punishment button.Name"))
                                .lore(LoreUtils.getLore("Empty punishment button.Lore", "{player}", target.getPlayerName(), "{category}", "ban"))
                                .build());
                        banGUI.setButton(13, emptyButton);
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                       player.openInventory(banGUI.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
