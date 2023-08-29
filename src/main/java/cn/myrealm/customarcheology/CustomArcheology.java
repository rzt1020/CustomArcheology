package cn.myrealm.customarcheology;

import cn.myrealm.customarcheology.commands.MainCommand;
import cn.myrealm.customarcheology.commands.subcommands.GiveCommand;
import cn.myrealm.customarcheology.commands.subcommands.HelpCommand;
import cn.myrealm.customarcheology.commands.subcommands.ReloadCommand;
import cn.myrealm.customarcheology.listeners.bukkit.PlayerJoinListener;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.utils.enums.Messages;
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
    private final List<AbstractManager> managers = new ArrayList<>();;

    @Override
    public void onEnable() {
        plugin = this;

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
        managers.add(new PlayerManager(this));
        managers.add(new BlockManager(this));
    }
    public void disablePlugin() {
        for (AbstractManager manager : managers) {
            manager.disable();
        }
    }

    public void registerDefaultListeners() {
        // Protocol Listener

        // Bukkit Listener
        new PlayerJoinListener(this).registerBukkitListener();
    }
    public void registerDefaultCommands() {
        MainCommand command = new MainCommand();
        command.registerSubCommand(new HelpCommand());
        command.registerSubCommand(new ReloadCommand());
        command.registerSubCommand(new GiveCommand());
        //noinspection ConstantConditions
        getCommand("customarcheology").setExecutor(command);
        //noinspection ConstantConditions
        getCommand("customarcheology").setTabCompleter(command);
    }

    static final List<String> FILES = Arrays.asList(
            "config.yml",
            "blocks/suspicious_stone.yml",
            "languages/zh_CN.yml");
    public void outputDefaultFiles() {
        for (String file : FILES) {
            saveResource(file, false);
        }
    }
}
