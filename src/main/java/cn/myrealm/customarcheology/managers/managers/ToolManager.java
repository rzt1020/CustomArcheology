package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.managers.BaseManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyTool;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author rzt1020
 */
public class ToolManager extends BaseManager {

    private static final String TOOL_PATH = CustomArcheology.plugin.getDataFolder().getPath() + "/tools/";
    private static ToolManager instance;
    private Map<String, ArcheologyTool> toolMap;
    public ToolManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static ToolManager getInstance() {
        return instance;
    }
    private static final String YML = ".yml";

    @Override
    protected void onInit() {
        toolMap = new HashMap<>(5);
        Path toolDirPath = Paths.get(TOOL_PATH);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Files.exists(toolDirPath)) {
                if (Files.isDirectory(toolDirPath)) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(toolDirPath, "*" + YML)) {
                        for (Path entry : stream) {
                            String toolName = entry.getFileName().toString().replace(YML, "");
                            toolMap.put(toolName, ArcheologyTool.loadFromFile(entry.toFile(), toolName));
                            Bukkit.getConsoleSender().sendMessage(Messages.TOOL_LOADED.getMessageWithPrefix("tool-id", toolName));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    Files.createDirectories(toolDirPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1L);
    }

    @Override
    protected void onDisable() {
        toolMap.values().forEach(ArcheologyTool::remove);
    }

    public ArcheologyTool getTool(String toolName) {
        if (toolMap.containsKey(toolName)) {
            return toolMap.get(toolName);
        }
        return null;
    }

    public boolean isToolExists(String toolName) {
        return toolMap.containsKey(toolName);
    }

    public Set<String> getToolsName() {
        return toolMap.keySet();
    }
}
