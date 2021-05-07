package me.zachary.historygui.guis;

import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.LoreUtils;
import me.zachary.historygui.utils.PunishmentUtils;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EditGui {
    private Historygui plugin;

    public EditGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openEditGui(Player player, me.zachary.historygui.player.Player target, String tableName, String reason, int id){
        ZMenu editGui = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Edit.Title name"), 1);
        editGui.setAutomaticPaginationEnabled(false);

        ZButton editReasonButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Edit.Edit.Icon")).parseItem())
        .name(plugin.getGuiConfig().getString("Gui.Edit.Edit.Name"))
        .lore(LoreUtils.getLore("Gui.Edit.Edit.Lore", "{reason}", reason))
        .build()).withListener(inventoryClickEvent -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
            });
            PunishmentUtils.editPunishment(plugin, player, tableName, id);
        });

        ZButton deleteButton = new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Edit.Delete.Icon")).parseItem())
                .name(plugin.getGuiConfig().getString("Gui.Edit.Delete.Name"))
                .lore(plugin.getGuiConfig().getStringList("Gui.Edit.Delete.Lore"))
                .build()).withListener(inventoryClickEvent -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.closeInventory();
            });
            PunishmentUtils.deletePunishment(plugin, player, tableName, id);
        });

        editGui.setButton(3, editReasonButton);
        editGui.setButton(5, deleteButton);

        player.openInventory(editGui.getInventory());
    }
}
