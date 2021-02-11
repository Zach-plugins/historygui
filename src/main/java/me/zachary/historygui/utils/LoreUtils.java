package me.zachary.historygui.utils;

import me.zachary.historygui.Historygui;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LoreUtils {
    private static final Historygui plugin = JavaPlugin.getPlugin(Historygui.class);

    public static List<String> getLore(String path, List<String> replace, List<String> replacement, List<String> remove, List<String> duration) {
        List<String> lore = new ArrayList<>();
        for (String l : plugin.getGuiConfig().getStringList(path)) {
            if(l.equals("{remove}")){
                l.replace("{remove}", "");
                lore.addAll(remove);
            }else if(l.equals("{duration}")){
                l.replace("{duration}", "");
                lore.addAll(duration);
            }else{
                for (int i = 0; i < replacement.size(); i++){
                    l = l.replace(replace.get(i), replacement.get(i));
                }
                lore.add(l);
            }
        }
        return lore;
    }

    public static List<String> getLore(String path, String oldChar1, String newChar1) {
        return getLore(path, oldChar1, newChar1, "", "");
    }

    public static List<String> getLore(String path, String oldChar1, String newChar1, String oldChar2, String newChar2) {
        List<String> lore = new ArrayList<>();
        for (String l : plugin.getGuiConfig().getStringList(path)) {
            lore.add(l.replace(oldChar1, String.valueOf(newChar1)).replace(oldChar2, String.valueOf(newChar2)));
        }
        return lore;
    }
}
