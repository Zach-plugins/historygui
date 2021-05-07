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
        browseGui.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, null, null, null, null));
        GuiUtils.setGlass(browseGui, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int slot = 10;
            int page = 0;
            for(me.zachary.historygui.player.Player p : plugin.getPlayerManager().getPlayers().values()){
                if(!plugin.getConfig().getBoolean("Offline player") && Bukkit.getPlayer(p.getUUID()) == null)
                    continue;
                ZButton playerButton = new ZButton(plugin.getConfig().getBoolean("Player head.Enable") ? getHeadItemDisplay(p) : new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("Player head.Item")).parseItem()).name(ChatUtils.colorCode("&7" + p.getPlayerName())).build())
                        .withListener(inventoryClickEvent -> {
                            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, p));
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
                player.updateInventory();
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.openInventory(browseGui.getInventory());
            });
        });
    }

    public ItemStack getHeadItemDisplay(me.zachary.historygui.player.Player op) {
        ItemStack item = new ItemStack(SkullUtils.getSkull(op.getUUID()).getType(), 1, (short) 3);
        SkullMeta pSkull = (SkullMeta) item.getItemMeta();
        pSkull.setDisplayName(ChatUtils.colorCode("&7" + op.getPlayerName()));
        pSkull.setOwner(op.getPlayerName());
        item.setItemMeta(pSkull);
        return item;
    }
}
