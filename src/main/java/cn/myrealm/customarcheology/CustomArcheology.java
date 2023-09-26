package cn.myrealm.customarcheology;

import cn.myrealm.customarcheology.commands.MainCommand;
import cn.myrealm.customarcheology.commands.subcommands.*;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.listeners.bukkit.BreakListener;
import cn.myrealm.customarcheology.listeners.bukkit.BrushListener;
import cn.myrealm.customarcheology.listeners.bukkit.PlayerListener;
import cn.myrealm.customarcheology.listeners.bukkit.PlaceListener;
import cn.myrealm.customarcheology.listeners.protocol.DigListener;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.managers.managers.*;
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
import java.util.Random;

/**
 * @author rzt1020
 */
public final class CustomArcheology extends JavaPlugin {
    public static CustomArcheology plugin;
    private final List<AbstractManager> managers = new ArrayList<>();
    public static ProtocolManager protocolManager;
    public static final Random RANDOM = new Random();

    @Override
    public void onEnable() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        outputDefaultFiles();

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
        managers.add(new DatabaseManager(this));
        managers.add(new LanguageManager(this));
        managers.add(new TextureManager(this));
        managers.add(new PlayerManager(this));
        managers.add(new LootManager(this));
        managers.add(new BlockManager(this));
        managers.add(new ChunkManager(this));
        managers.add(new ToolManager(this));
    }
    public void disablePlugin() {
        for (AbstractManager manager : managers) {
            manager.disable();
        }
    }

    public void registerDefaultListeners() {
        // Protocol Listener
        new DigListener(this).registerProtocolListener();
        // Bukkit Listener
        new PlayerListener(this).registerBukkitListener();
        new PlaceListener(this).registerBukkitListener();
        new BreakListener(this).registerBukkitListener();
        new BrushListener(this).registerBukkitListener();
    }
    public void registerDefaultCommands() {
        MainCommand command = new MainCommand();
        command.registerSubCommand(new HelpCommand());
        command.registerSubCommand(new ReloadCommand());
        command.registerSubCommand(new GiveCommand());
        command.registerSubCommand(new TestCommand());
        command.registerSubCommand(new ArchifyCommand());
        command.registerSubCommand(new DeArchifyCommand());
        //noinspection ConstantConditions
        getCommand("customarcheology").setExecutor(command);
        //noinspection ConstantConditions
        getCommand("customarcheology").setTabCompleter(command);
    }

    static final List<String> FILES = Arrays.asList(
            "config.yml",
            "pack/pack.mcmeta",
            "pack/pack.png",
            "pack/assets/minecraft/models/item/brush_brushing_0.json",
            "pack/assets/minecraft/models/item/brush_brushing_1.json",
            "pack/assets/minecraft/models/item/brush_brushing_2.json",
            "pack/assets/minecraft/models/item/brush_brushing_3.json",
            "pack/assets/minecraft/models/item/brush_brushing_4.json",
            "pack/assets/minecraft/models/item/brush_brushing_5.json",
            "pack/assets/minecraft/models/item/brush_brushing_6.json",
            "pack/assets/minecraft/models/item/brush_brushing_7.json",
            "pack/assets/minecraft/models/item/brush_brushing_8.json",
            "pack/assets/minecraft/models/item/brush_brushing_9.json",
            "pack/assets/minecraft/models/item/brush_brushing_10.json",
            "blocks/suspicious_stone.yml",
            "blocks/suspicious_netherrack.yml",
            "blocks/suspicious_end_stone.yml",
            "blocks/suspicious_dirt.yml",
            "blocks/suspicious_deepslate.yml",
            "blocks/suspicious_sculk.yml",
            "tools/diamond_brush.yml",
            "tools/netherite_brush.yml",
            "tools/archaeological_shovel.yml",
            "tools/diamond_archaeological_shovel.yml",
            "tools/netherite_archaeological_shovel.yml",
            "textures/blocks/suspicious_stone_0.png",
            "textures/blocks/suspicious_stone_1.png",
            "textures/blocks/suspicious_stone_2.png",
            "textures/blocks/suspicious_stone_3.png",
            "textures/blocks/suspicious_netherrack_0.png",
            "textures/blocks/suspicious_netherrack_1.png",
            "textures/blocks/suspicious_netherrack_2.png",
            "textures/blocks/suspicious_netherrack_3.png",
            "textures/blocks/suspicious_dirt_0.png",
            "textures/blocks/suspicious_dirt_1.png",
            "textures/blocks/suspicious_dirt_2.png",
            "textures/blocks/suspicious_dirt_3.png",
            "textures/blocks/suspicious_end_stone_0.png",
            "textures/blocks/suspicious_end_stone_1.png",
            "textures/blocks/suspicious_end_stone_2.png",
            "textures/blocks/suspicious_end_stone_3.png",
            "textures/blocks/suspicious_deepslate_0.png",
            "textures/blocks/suspicious_deepslate_1.png",
            "textures/blocks/suspicious_deepslate_2.png",
            "textures/blocks/suspicious_deepslate_3.png",
            "textures/blocks/suspicious_sculk_0.png",
            "textures/blocks/suspicious_sculk_1.png",
            "textures/blocks/suspicious_sculk_2.png",
            "textures/blocks/suspicious_sculk_3.png",
            "textures/blocks/suspicious_sculk_0.png.mcmeta",
            "textures/blocks/suspicious_sculk_1.png.mcmeta",
            "textures/blocks/suspicious_sculk_2.png.mcmeta",
            "textures/blocks/suspicious_sculk_3.png.mcmeta",
            "textures/tools/archaeological_shovel.png",
            "textures/tools/diamond_archaeological_shovel.png",
            "textures/tools/netherite_archaeological_shovel.png",
            "textures/tools/diamond_brush.png",
            "textures/tools/netherite_brush.png",
            "languages/zh_CN.yml",
            "loottables/stone.yml");
    static final List<String> FILES_LOW_VERSION = Arrays.asList(
            "pack/assets/minecraft/models/block/barrier.json",
            "pack/assets/minecraft/textures/item/nothing.png");
    static final String NEWEST_VERSION = "1.20.2";
    public void outputDefaultFiles() {
        FILES.forEach(file -> {
            try {
                saveResource(file, false);
            } catch (Exception e) {
                String[] names = file.split("/");
                Bukkit.getConsoleSender().sendMessage(Messages.ERROR_MISSING_RESOURCE.getMessageWithPrefix("resource-name", names[names.length - 1]));
            }
        });
        if (!Bukkit.getVersion().contains(NEWEST_VERSION)) {
            FILES_LOW_VERSION.forEach(file -> {
                try {
                    saveResource(file, false);
                } catch (Exception e) {
                    String[] names = file.split("/");
                    Bukkit.getConsoleSender().sendMessage(Messages.ERROR_MISSING_RESOURCE.getMessageWithPrefix("resource-name", names[names.length - 1]));
                }
            });
        }
    }
}
