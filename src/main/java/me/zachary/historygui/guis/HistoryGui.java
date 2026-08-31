package me.zachary.historygui.guis;

import com.zachfr.zachlibrary.library.spigui.buttons.SGButton;
import com.zachfr.zachlibrary.library.spigui.menu.SGMenu;
import com.zachfr.zachlibrary.library.xseries.XMaterial;
import com.zachfr.zachlibrary.utils.ItemBuilder;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HistoryGui {
    private Historygui plugin;

    public HistoryGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public Inventory getHistoryInventory(Player player, me.zachary.historygui.player.Player target) {
        return getHistoryInventory(player, target, false);
    }

    public Inventory getHistoryInventory(Player player, me.zachary.historygui.player.Player target, Boolean fromCommand){
        SGMenu historyGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Main.Title name").replace("{target}", target.getPlayerName()), 3);
        historyGUI.setAutomaticPaginationEnabled(true);
        historyGUI.setToolbarBuilder(GuiUtils.getPaginationButtonBuilder(player, target, () -> {
            new BrowseGui(plugin).openBrowseGui(player);
        }, null, fromCommand, null));
        GuiUtils.setGlass(historyGUI, 0);


        SGButton banButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Main.Ban.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Main.Ban.Icon name")).build()).withListener(inventoryClickEvent -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        new BanGui(plugin).openBanInventory(player, target, fromCommand);
                    });
        });

        SGButton kickButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Main.Kick.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Main.Kick.Icon name")).build()).withListener(inventoryClickEvent -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        new KickGui(plugin).openKickInventory(player, target, fromCommand);
                    });
        });

        SGButton muteButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Main.Mute.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Main.Mute.Icon name")).build()).withListener(inventoryClickEvent -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        new MuteGui(plugin).openMuteInventory(player, target, fromCommand);
                    });
        });

        SGButton warningButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Main.Warning.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Main.Warning.Icon name")).build()).withListener(inventoryClickEvent -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        new WarningGui(plugin).openWarningInventory(player, target, fromCommand);
                    });
        });

        historyGUI.setButton(10, banButton);
        historyGUI.setButton(12, kickButton);
        historyGUI.setButton(14, muteButton);
        historyGUI.setButton(16, warningButton);

        return historyGUI.getInventory();
    }
}
