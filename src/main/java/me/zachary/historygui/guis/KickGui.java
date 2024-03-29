package me.zachary.historygui.guis;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
import me.zachary.historygui.utils.LoreUtils;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
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

public class KickGui {
    private Historygui plugin;
    private Boolean sort = false;

    public KickGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openKickInventory(Player player, me.zachary.historygui.player.Player target) {
        openKickInventory(player, target, false);
    }

    public void openKickInventory(Player player, me.zachary.historygui.player.Player target, Boolean fromCommand){
        ZMenu kickGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Kick.Title name").replace("{target}", target.getPlayerName()), plugin.getGuiConfig().getInt("Gui.Kick.Row") + 2);
        kickGUI.setAutomaticPaginationEnabled(true);
        kickGUI.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, target, () -> {
            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target, fromCommand));
        }, sort, inventoryClickEvent -> {
            sort = !sort;
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
                openKickInventory(player, target);
            });
        }));
        GuiUtils.setGlass(kickGUI, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query;
            if(sort)
                query = "SELECT * FROM {kicks} WHERE uuid=? ORDER BY TIME ASC";
            else
                query = "SELECT * FROM {kicks} WHERE uuid=? ORDER BY TIME DESC";
            int slot = 10;
            int page = 0;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, String.valueOf(target.getUUID()));
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        List<String> replace = new ArrayList<>();
                        List<String> replacement = new ArrayList<>();
                        replace.add("{name}");
                        replacement.add(rs.getString("banned_by_name"));
                        replace.add("{server}");
                        replacement.add(rs.getString("server_origin") != null ? rs.getString("server_origin") : "Server");
                        replace.add("{reason}");
                        replacement.add(rs.getString("reason"));

                        String reason = rs.getString("reason");
                        int id = rs.getInt("id");
                        ZButton banButton = new ZButton(new ItemBuilder(rs.getString("active").equals("1") ? XMaterial.LIME_WOOL.parseItem() : XMaterial.RED_WOOL.parseItem())
                                .name(plugin.getGuiConfig().getString("Gui.Kick.Icon name").replace("{time}", new SimpleDateFormat(plugin.getGuiConfig().getString("Date fomat")).format(new Date(rs.getLong("time")))))
                                .lore(LoreUtils.getLore("Gui.Kick.Content", replace, replacement, null, null))
                                .build()).withListener(inventoryClickEvent -> {
                            if(plugin.getConfig().getBoolean("Allow edit punishment") && player.hasPermission("historygui.edit.kick"))
                                new EditGui(plugin).openEditGui(player, target, "{kicks}", reason, id);
                        });

                        kickGUI.setButton(page, slot, banButton);

                        if(plugin.getGuiConfig().getInt("Gui.Kick.Row") == 1){
                            slot++;
                            if(slot == 17){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(kickGUI, page);
                            }
                        } else if(plugin.getGuiConfig().getInt("Gui.Kick.Row") == 2){
                            if(slot == 16)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 26){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(kickGUI, page);
                            }
                        }else if(plugin.getGuiConfig().getInt("Gui.Kick.Row") == 3){
                            if(slot == 16 || slot == 25)
                                slot += 3;
                            else
                                slot++;
                            if(slot == 35){
                                slot = 10;
                                page++;
                                GuiUtils.setGlass(kickGUI, page);
                            }
                        }else
                            slot++; // ?
                    }
                    if(kickGUI.getButton(10) == null){
                        ZButton emptyButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Empty punishment button.Item")).parseItem())
                                .name(plugin.getGuiConfig().getString("Empty punishment button.Name"))
                                .lore(LoreUtils.getLore("Empty punishment button.Lore", "{player}", target.getPlayerName(), "{category}", "kick"))
                                .build());
                        kickGUI.setButton(13, emptyButton);
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(kickGUI.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
