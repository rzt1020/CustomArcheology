package cn.myrealm.customarcheology;

import cn.myrealm.customarcheology.commands.MainCommand;
import cn.myrealm.customarcheology.commands.subcommands.GiveCommand;
import cn.myrealm.customarcheology.commands.subcommands.HelpCommand;
import cn.myrealm.customarcheology.commands.subcommands.ReloadCommand;
import cn.myrealm.customarcheology.commands.subcommands.TestCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.listeners.bukkit.BlockBreakListener;
import cn.myrealm.customarcheology.listeners.bukkit.PlayerBrushListener;
import cn.myrealm.customarcheology.listeners.bukkit.PlayerJoinListener;
import cn.myrealm.customarcheology.listeners.bukkit.PlayerPlaceBlockListener;
import cn.myrealm.customarcheology.listeners.protocol.PlayerDigListener;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.LootManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.managers.managers.system.DatabaseManager;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.system.TextureManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rzt10
 */
public final class CustomArcheology extends JavaPlugin {
    public static CustomArcheology plugin;
    private final List<AbstractManager> managers = new ArrayList<>();
    public static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        if (!getDataFolder().exists()) {
            outputDefaultFiles();
        }

        initPlugin();
        registerDefaultListeners();
        registerDefaultCommands();

        Bukkit.getConsoleSender().sendMessage(Messages.ENABLE_MESSAGE.getMessageWithPrefix());
    }

    @Override
    public void onDisable() {
        disablePlugin();
    }

    public void reloadPlugin() {
        disablePlugin();
        initPlugin();
    }
    public void initPlugin() {
        managers.clear();
        managers.add(new LanguageManager(this));
        managers.add(new DatabaseManager(this));
        managers.add(new TextureManager(this));
        managers.add(new PlayerManager(this));
        managers.add(new LootManager(this));
        managers.add(new BlockManager(this));
        managers.add(new ChunkManager(this));
    }
    public void disablePlugin() {
        for (AbstractManager manager : managers) {
            manager.disable();
        }
    }

    public void registerDefaultListeners() {
        // Protocol Listener
        new PlayerDigListener(this).registerProtocolListener();
        // Bukkit Listener
        new PlayerJoinListener(this).registerBukkitListener();
        new PlayerPlaceBlockListener(this).registerBukkitListener();
        new BlockBreakListener(this).registerBukkitListener();
        new PlayerBrushListener(this).registerBukkitListener();
    }
    public void registerDefaultCommands() {
        MainCommand command = new MainCommand();
        command.registerSubCommand(new HelpCommand());
        command.registerSubCommand(new ReloadCommand());
        command.registerSubCommand(new GiveCommand());
        command.registerSubCommand(new TestCommand());
        //noinspection ConstantConditions
        getCommand("customarcheology").setExecutor(command);
        //noinspection ConstantConditions
        getCommand("customarcheology").setTabCompleter(command);
    }

    static final List<String> FILES = Arrays.asList(
            "config.yml",
            "pack/pack.mcmeta",
            "blocks/suspicious_stone.yml",
            "textures/blocks/suspicious_stone.png",
            "textures/blocks/suspicious_stone_1.png",
            "textures/blocks/suspicious_stone_2.png",
            "textures/blocks/suspicious_stone_3.png",
            "languages/zh_CN.yml");
    public void outputDefaultFiles() {
        for (String file : FILES) {
            try {
                saveResource(file, false);
            } catch (Exception e) {
                String[] names = file.split("/");
                Bukkit.getConsoleSender().sendMessage(Messages.MISSING_RESOURCE.getMessageWithPrefix("resource-name", names[names.length - 1]));
            }
        }
    }
}
