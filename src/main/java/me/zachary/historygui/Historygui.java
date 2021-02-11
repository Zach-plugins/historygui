package me.zachary.historygui;

import me.zachary.historygui.commands.HistoryCommand;
import me.zachary.zachcore.ZachCorePlugin;
import me.zachary.zachcore.guis.ZachGUI;
import me.zachary.zachcore.utils.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class Historygui extends ZachCorePlugin {
    public static ZachGUI zachGUI;
    private File guiFile;
    private File messageFile;
    public YamlConfiguration guiConfig;
    public YamlConfiguration messageConfig;

    @Override
    public void onEnable() {
        zachGUI = new ZachGUI(this);
        loadGuiConfig();
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

    public void loadGuiConfig() {
        guiFile = new File(getDataFolder() + File.separator + "gui.yml");

        if (!guiFile.exists()) {
            saveResource("gui.yml", false);
            guiFile = new File(getDataFolder() + File.separator + "gui.yml");
        }

        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
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
        guiFile = new File(getDataFolder() + File.separator + "gui.yml");
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
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
}
