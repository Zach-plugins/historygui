package me.zachary.historygui;

import com.zachfr.zachlibrary.ZachLibrary;
import com.zachfr.zachlibrary.library.spigui.SpiGUI;
import com.zachfr.zachlibrary.utils.Config;
import com.zachfr.zachlibrary.utils.Metrics;
import me.zachary.historygui.commands.HistoryCommand;
import me.zachary.historygui.commands.StaffHistoryCommand;
import me.zachary.historygui.listeners.JoinListener;
import me.zachary.historygui.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Historygui extends ZachLibrary {
    private static Historygui instance;
    public static SpiGUI zachGUI;
    private File guiFile;
    public Config guiConfig = new Config();

    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        zachGUI = new SpiGUI(this);
        reloadGuiConfig();
        loadLanguage();
        saveDefaultConfig();
        new HistoryCommand(this);
        new StaffHistoryCommand(this);

        playerManager = new PlayerManager(this);

        new JoinListener(this);

        Metrics metrics = new Metrics(this, 10290);

        preEnable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Historygui getInstance() {
        return instance;
    }

    public static SpiGUI getGUI() {
        return zachGUI;
    }

    public void reloadGuiConfig() {
        if (!new File(this.getDataFolder(), "gui.yml").exists()) {
            saveResource("gui.yml", false);
        }

        guiConfig.load(new File(getDataFolder() + File.separator + "gui.yml"),
                getResource("gui.yml"), "");
    }

    public YamlConfiguration getGuiConfig(){
        return guiConfig;
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    @Override
    public void onDataLoad() {

    }
}
