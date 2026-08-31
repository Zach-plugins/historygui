package me.zachary.historygui.guis;

import com.zachfr.zachlibrary.library.spigui.buttons.SGButton;
import com.zachfr.zachlibrary.library.spigui.menu.SGMenu;
import com.zachfr.zachlibrary.library.xseries.XMaterial;
import com.zachfr.zachlibrary.utils.ItemBuilder;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.LoreUtils;
import me.zachary.historygui.utils.PunishmentUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EditGui {
    private Historygui plugin;

    public EditGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openEditGui(Player player, me.zachary.historygui.player.Player target, String tableName, String reason, int id){
        SGMenu editGui = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Edit.Title name"), 1);
        editGui.setAutomaticPaginationEnabled(false);

        SGButton editReasonButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Edit.Edit.Icon")).parseItem())
        .name(plugin.getGuiConfig().getString("Gui.Edit.Edit.Name"))
        .lore(LoreUtils.getLore("Gui.Edit.Edit.Lore", "{reason}", reason))
        .build()).withListener(inventoryClickEvent -> {
            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
            PunishmentUtils.editPunishment(plugin, player, tableName, id);
        });

        SGButton removePunishment = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Edit.Remove.Icon")).parseItem())
        .name(plugin.getGuiConfig().getString("Gui.Edit.Remove.Name"))
        .lore(plugin.getGuiConfig().getStringList("Gui.Edit.Remove.Lore"))
        .build()).withListener(inventoryClickEvent -> {
            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
            PunishmentUtils.removePunishment(plugin, player, target, tableName, id);
        });

        SGButton deleteButton = new SGButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Edit.Delete.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Edit.Delete.Name"))
                .lore(plugin.getGuiConfig().getStringList("Gui.Edit.Delete.Lore"))
                .build()).withListener(inventoryClickEvent -> {
            Bukkit.getScheduler().runTask(plugin, player::closeInventory);
            PunishmentUtils.deletePunishment(plugin, player, tableName, id);
        });

        if(player.hasPermission("historygui.edit.reason"))
            editGui.setButton(2, editReasonButton);
        if(player.hasPermission("historygui.edit.remove"))
            editGui.setButton(4, removePunishment);
        if(player.hasPermission("historygui.edit.delete"))
            editGui.setButton(6, deleteButton);


        player.openInventory(editGui.getInventory());
    }
}
