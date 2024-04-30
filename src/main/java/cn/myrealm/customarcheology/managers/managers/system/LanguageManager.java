package cn.myrealm.customarcheology.managers.managers.system;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.BaseManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rzt1020
 */
public class LanguageManager extends BaseManager {

    private static LanguageManager instance;
    private String language;
    private FileConfiguration languageConfig;
    private FileConfiguration tempLanguageConfig;

    public LanguageManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    protected void onInit() {
        language = Config.CONFIG_FILES_LANGUAGE.asString();
        loadLanguageFile();

    }

    public static LanguageManager getInstance() {
        return instance;
    }

    private void loadLanguageFile() {
        File languageFile = new File(plugin.getDataFolder(), "languages/" + language + ".yml");
        languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        InputStream is = CustomArcheology.plugin.getResource("languages/en_US.yml");
        if (is == null) {
            return;
        }
        File tempFile = new File(CustomArcheology.plugin.getDataFolder(), "tempMessage.yml");
        try {
            Files.copy(is, tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempLanguageConfig = YamlConfiguration.loadConfiguration(tempFile);
        tempFile.delete();
    }

    public String getMessage(String path) throws Exception{
        String message = languageConfig.getString(path);
        if (Objects.isNull(message)) {
            message = tempLanguageConfig.getString(path);
            if (Objects.isNull(message)) {
                throw new Exception("Incorrect language key");
            }
        }
        return parseColor(message);
    }

    static final Pattern PATTERN = Pattern.compile("<#[a-fA-F0-9]{6}>");
    public static String parseColor(String message) {
        Matcher match = PATTERN.matcher(message);
        while (match.find()) {
            String color = message.substring(match.start(),match.end());
            message = message.replace(color, ChatColor.of(color.replace("<","").replace(">","")) + "");
            match = PATTERN.matcher(message);
        }
        return message.replace("&","§").replace("§§","&");
    }
}
