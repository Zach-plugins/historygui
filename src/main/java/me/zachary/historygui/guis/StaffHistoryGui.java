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
import org.bukkit.inventory.Inventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StaffHistoryGui {

	public enum Type {
		All(Historygui.getInstance().getGuiConfig().getString("Sort staff placeholder.All")),
		Ban(Historygui.getInstance().getGuiConfig().getString("Sort staff placeholder.Ban")),
		Mute(Historygui.getInstance().getGuiConfig().getString("Sort staff placeholder.Mute")),
		Warning(Historygui.getInstance().getGuiConfig().getString("Sort staff placeholder.Warning")),
		Kick(Historygui.getInstance().getGuiConfig().getString("Sort staff placeholder.Kick"));

		private String name;
		Type(String string) {
			this.name = string;
		}

		public String getName() {
			return name;
		}

		public static Type getType(String string) {
			for(Type type : Type.values()) {
				if(type.getName().equals(string)) {
					return type;
				}
			}
			return null;
		}
	}

	private Historygui plugin;

	private Player player;
	private OfflinePlayer target;
	private Boolean sort = true;
	private Type type = Type.All;

	private int all = 0;
	private int ban = 0;
	private int mute = 0;
	private int warning = 0;
	private int kick = 0;

	public StaffHistoryGui(Historygui plugin, Player player, OfflinePlayer target) {
		this.plugin = plugin;
		this.player = player;
		this.target = target;
	}

	public Inventory getInventory() {
		ZMenu historyGUI = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Staff.Title name").replace("{name}", String.valueOf((target.getName() != null ? target.getName() : target.getUniqueId()))), 5);
		historyGUI.setAutomaticPaginationEnabled(true);
		GuiUtils.setGlass(historyGUI, 0);

		// Get all punishment from that staff.
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			String bans = "SELECT uuid, banned_by_name, server_origin, reason, removed_by_name, removed_by_reason, removed_by_date, until, time, 'Ban' AS `type`, active FROM {bans} WHERE banned_by_uuid = ?";
			String mutes = "SELECT uuid, banned_by_name, server_origin, reason, removed_by_name, removed_by_reason, removed_by_date, until, time, 'Mute' AS `type`, active FROM {mutes} WHERE banned_by_uuid = ?";
			String warnings = "SELECT uuid, banned_by_name, server_origin, reason, removed_by_name, removed_by_reason, removed_by_date, until, time, 'Warning' AS `type`, active FROM {warnings} WHERE banned_by_uuid = ?";
			String kicks = "SELECT uuid, banned_by_name, server_origin, reason, null AS `removed_by_name`, null AS `removed_by_reason`, null AS `removed_by_date`, until, time, 'Kick' AS `type`, active FROM {kicks} WHERE banned_by_uuid = ?";
			String query;
			switch (type){
				case Ban:
					query = bans;
					break;
				case Mute:
					query = mutes;
					break;
				case Warning:
					query = warnings;
					break;
				case Kick:
					query = kicks;
					break;
				default:
					query = bans + " UNION " + mutes + " UNION " + warnings + " UNION " + kicks;
					break;
			}
			try (PreparedStatement st = Database.get().prepareStatement(query + " ORDER BY time " + (sort ? "DESC" : "ASC"))) {
				try {
					st.setString(1, String.valueOf(target.getUniqueId()));
					st.setString(2, String.valueOf(target.getUniqueId()));
					st.setString(3, String.valueOf(target.getUniqueId()));
					st.setString(4, String.valueOf(target.getUniqueId()));
				} catch (SQLException ignored) {}
				try (ResultSet rs = st.executeQuery()) {
					int slot = 10;
					int page = 0;
					if(type.equals(Type.All)) {
						all = 0;
						ban = 0;
						mute = 0;
						warning = 0;
						kick = 0;
					}
					while(rs.next()) {
						if(type.equals(Type.All)){
							all++;
							if(rs.getString("type").equals("Ban")) {
								ban++;
							} else if(rs.getString("type").equals("Mute")) {
								mute++;
							} else if(rs.getString("type").equals("Warning")) {
								warning++;
							} else if(rs.getString("type").equals("Kick")) {
								kick++;
							}
						}

						List<String> replace = new ArrayList<>();
						List<String> replacement = new ArrayList<>();
						List<String> remove = new ArrayList<>();
						List<String> duration = new ArrayList();
						replace.add("{name}");
						replacement.add(rs.getString("banned_by_name"));
						replace.add("{date}");
						replacement.add(new SimpleDateFormat(plugin.getGuiConfig().getString("Date fomat")).format(new Date(rs.getLong("time"))));
						replace.add("{type}");
						replacement.add(rs.getString("type"));
						replace.add("{target}");
						me.zachary.historygui.player.Player targetPlayer = plugin.getPlayerManager().getPlayer(UUID.fromString(rs.getString("uuid")));
						replacement.add(targetPlayer != null ? targetPlayer.getPlayerName() : rs.getString("uuid"));
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
							rRemove.add("{reason}");
							rrRemove.add(rs.getString("removed_by_reason"));
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

						boolean active = rs.getString("active").equals("1");
						ZButton button = new ZButton(new ItemBuilder(active ? XMaterial.LIME_WOOL.parseItem() : XMaterial.RED_WOOL.parseItem())
								.name(plugin.getGuiConfig().getString("Gui.Staff.Icon name")
										.replace("{date}", new SimpleDateFormat(plugin.getGuiConfig().getString("Date fomat")).format(new Date(rs.getLong("time"))))
										.replace("{type}", rs.getString("type"))
								)
								.lore(LoreUtils.getLore("Gui.Staff.Content", replace, replacement, remove, duration))
								.build());

						historyGUI.setButton(page, slot, button);
						historyGUI.refreshInventory(player);

						if(slot == 16 || slot == 25)
							slot += 3;
						else
							slot++;
						if(slot == 35){
							slot = 10;
							page++;
							GuiUtils.setGlass(historyGUI, page);
						}
					}
					historyGUI.setPaginationButtonBuilder(GuiUtils.getStaffPaginationButtonBuilder(player, target, sort, inventoryClickEvent -> {
						sort = !sort;
						Bukkit.getScheduler().runTask(plugin, () -> {
							player.closeInventory();
							player.openInventory(getInventory());
						});
					}, type.getName(), inventoryClickEvent -> {
						int ordinal = type.ordinal();
						if(inventoryClickEvent.getClick().isLeftClick()){
							if(ordinal == 0) {
								ordinal = Type.values().length - 1;
							} else {
								ordinal--;
							}
						} else if(inventoryClickEvent.getClick().isRightClick()) {
							if(ordinal == Type.values().length - 1) {
								ordinal = 0;
							} else {
								ordinal++;
							}
						}
						type = Type.values()[ordinal];
						Bukkit.getScheduler().runTask(plugin, () -> {
							player.closeInventory();
							player.openInventory(getInventory());
						});
					}, all, ban, mute, warning, kick));
					historyGUI.refreshInventory(player);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		return historyGUI.getInventory();
	}
}
