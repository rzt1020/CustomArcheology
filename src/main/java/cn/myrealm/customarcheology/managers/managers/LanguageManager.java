package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.utils.enums.Config;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rzt10
 */
public class LanguageManager extends AbstractManager {

    private static LanguageManager instance;
    private String language;
    private FileConfiguration languageConfig;

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
    }

    public String getMessage(String path) throws Exception{
        String message = languageConfig.getString(path);
        if (Objects.isNull(message)) {
            throw new Exception("Incorrect language key");
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
