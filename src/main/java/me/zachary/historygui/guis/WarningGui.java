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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WarningGui {
    private Historygui plugin;

    public WarningGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openWarningInventory(Player player, OfflinePlayer target){
        ZMenu warningGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Warning.Title name").replace("{target}", target.getName()), 3);
        warningGUI.setAutomaticPaginationEnabled(true);
        warningGUI.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, target, () -> {
            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target));
        }));
        GuiUtils.setGlass(warningGUI, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "SELECT * FROM {warnings} WHERE uuid=?";
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
                        replacement.add(rs.getString("server_origin"));
                        replace.add("{reason}");
                        replacement.add(rs.getString("reason"));

                        String reason = rs.getString("reason");
                        int id = rs.getInt("id");
                        ZButton banButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Warning.Icon")).parseItem())
                                .name(plugin.getGuiConfig().getString("Gui.Warning.Icon name").replace("{time}", new Date(rs.getLong("time")).toString()))
                                .lore(LoreUtils.getLore("Gui.Warning.Content", replace, replacement, remove, duration))
                                .build()).withListener(inventoryClickEvent -> {
                            if(plugin.getConfig().getBoolean("Allow edit punishment"))
                                new EditGui(plugin).openEditGui(player, target, "{warnings}", reason, id);
                        });

                        warningGUI.setButton(page, slot, banButton);

                        slot++;
                        if(slot == 17){
                            slot = 0;
                            page++;
                            GuiUtils.setGlass(warningGUI, page);
                        }
                    }
                    if(warningGUI.getButton(10) == null){
                        ZButton emptyButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Empty punishment button.Item")).parseItem())
                        .name(plugin.getGuiConfig().getString("Empty punishment button.Name"))
                        .lore(LoreUtils.getLore("Empty punishment button.Lore", "{player}", target.getName(), "{category}", "warning"))
                        .build());
                        warningGUI.setButton(13, emptyButton);
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(warningGUI.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
