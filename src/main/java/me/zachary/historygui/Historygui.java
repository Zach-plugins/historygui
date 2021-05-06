package me.zachary.historygui;

import me.zachary.historygui.commands.HistoryCommand;
import me.zachary.zachcore.ZachCorePlugin;
import me.zachary.zachcore.config.Config;
import me.zachary.zachcore.guis.ZachGUI;
import me.zachary.zachcore.utils.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Historygui extends ZachCorePlugin {
    public static ZachGUI zachGUI;
    private File guiFile;
    private File messageFile;
    public Config guiConfig = new Config();
    public YamlConfiguration messageConfig;

    @Override
    public void onEnable() {
        zachGUI = new ZachGUI(this);
        reloadGuiConfig();
        loadMessageConfig();
        saveDefaultConfig();
        new HistoryCommand(this);
        Metrics metrics = new Metrics(this, 10290);

        preEnable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ZachGUI getGUI() {
        return zachGUI;
    }

    public void loadMessageConfig() {
        messageFile = new File(getDataFolder() + File.separator + "messages.yml");

        if (!messageFile.exists()) {
            saveResource("messages.yml", false);
            messageFile = new File(getDataFolder() + File.separator + "messages.yml");
        }

        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    public void reloadGuiConfig() {
        if (!new File(this.getDataFolder(), "gui.yml").exists()) {
            saveResource("gui.yml", false);
        }

        guiConfig.load(new File(getDataFolder() + File.separator + "gui.yml"),
                getResource("gui.yml"), "");
    }

    public void reloadMessageConfig() {
        messageFile = new File(getDataFolder() + File.separator + "messages.yml");
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    public YamlConfiguration getGuiConfig(){
        return guiConfig;
    }

    public YamlConfiguration getMessageConfig(){
        return messageConfig;
    }

    @Override
    public void onDataLoad() {

    }
}
