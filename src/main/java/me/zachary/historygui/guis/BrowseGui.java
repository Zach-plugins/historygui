package me.zachary.historygui.guis;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.utils.ChatUtils;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.SkullUtils;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BrowseGui {
    private Historygui plugin;

    public BrowseGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openBrowseGui(Player player){
        ZMenu browseGui = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Browse.Title name"), 5);
        browseGui.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, null, null));
        GuiUtils.setGlass(browseGui, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "select * from {history} WHERE uuid != 'CONSOLE'";
            int slot = 10;
            int page = 0;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                        if(!plugin.getConfig().getBoolean("Offline player") && target.getPlayer() == null)
                            continue;
                        if(target.getName() == null)
                            continue;
                        ZButton playerButton = new ZButton(getHeadItemDisplay(target))
                                .withListener(inventoryClickEvent -> {
                            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target));
                        });

                        browseGui.setButton(page, slot, playerButton);

                        slot++;
                        if(slot == 17 || slot == 26)
                            slot += 2;
                        if(slot == 35){
                            slot = 10;
                            page++;
                            GuiUtils.setGlass(browseGui, page);
                        }
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(browseGui.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public ItemStack getHeadItemDisplay(OfflinePlayer op) {
        ItemStack item = new ItemStack(SkullUtils.getSkull(op.getUniqueId()).getType(), 1, (short) 3);
        SkullMeta pSkull = (SkullMeta) item.getItemMeta();
        pSkull.setDisplayName(ChatUtils.colorCode("&7" + op.getName()));
        pSkull.setOwner(op.getName());
        item.setItemMeta(pSkull);
        return item;
    }
}
