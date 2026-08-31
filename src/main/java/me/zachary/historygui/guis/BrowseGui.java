package me.zachary.historygui.guis;

import com.zachfr.zachlibrary.library.spigui.buttons.SGButton;
import com.zachfr.zachlibrary.library.spigui.menu.SGMenu;
import com.zachfr.zachlibrary.library.xseries.XMaterial;
import com.zachfr.zachlibrary.library.xseries.profiles.builder.XSkull;
import com.zachfr.zachlibrary.library.xseries.profiles.objects.Profileable;
import com.zachfr.zachlibrary.utils.ColorUtils;
import com.zachfr.zachlibrary.utils.ItemBuilder;
import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
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
        SGMenu browseGui = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Browse.Title name"), 5);
        browseGui.setToolbarBuilder(GuiUtils.getPaginationButtonBuilder(player, null, null, null, null));
        GuiUtils.setGlass(browseGui, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int slot = 10;
            int page = 0;
            for(me.zachary.historygui.player.Player p : plugin.getPlayerManager().getPlayers().values()){
                if(!plugin.getConfig().getBoolean("Offline player") && Bukkit.getPlayer(p.getUUID()) == null)
                    continue;
                SGButton playerButton = new SGButton(plugin.getConfig().getBoolean("Player head.Enable") ? getHeadItemDisplay(p) : new ItemBuilder(XMaterial.valueOf(plugin.getConfig().getString("Player head.Item")).parseItem()).name(ColorUtils.colorCode("&7" + p.getPlayerName())).build())
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
        ItemStack item = XSkull.createItem().profile(Profileable.of(op.getUUID())).apply();
        SkullMeta pSkull = (SkullMeta) item.getItemMeta();
        pSkull.setDisplayName(ColorUtils.colorCode("&7" + op.getPlayerName()));
        item.setItemMeta(pSkull);
        return item;
    }
}
