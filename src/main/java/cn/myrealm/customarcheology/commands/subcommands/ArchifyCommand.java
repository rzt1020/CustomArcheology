package cn.myrealm.customarcheology.commands.subcommands;


import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.mechanics.cores.ArcheologyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author rzt1020
 */
public class ArchifyCommand implements SubCommand {
    @Override
    public String getName() {
        return "archify";
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_ARCHIFY.getMessage();
    }

    @Override
    public String getUsage() {
        return "/customarcheology archify <BlockID> <World>";
    }

    @Override
    public List<String> getSubCommandAliases() {
        return List.of("arch");
    }

    @Override
    public List<String> onTabComplete(int argsNum) {
        List<String> suggestions = new ArrayList<>();
        if (argsNum == FIRST_ARGUMENT) {
            BlockManager blockManager = BlockManager.getInstance();
            suggestions.addAll(blockManager.getBlocksName());
        } else if (argsNum == SECOND_ARGUMENT) {
            suggestions.addAll(Bukkit.getWorlds().stream().map(World::getName).toList());
        }
        return suggestions;
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!Permissions.ARCHIFY.hasPermission(sender)) {
            return;
        }
        if (args.length != TWO_ARGUMENTS) {
            sender.sendMessage(this.getUsage());
        }
        BlockManager blockManager = BlockManager.getInstance();
        if (blockManager.isBlockExists(args[FIRST_ARGUMENT])) {
            World world = Bukkit.getWorld(args[SECOND_ARGUMENT]);
            if (Objects.nonNull(world)) {
                blockManager.setWorldBlock(world, args[FIRST_ARGUMENT]);
                Set<String> blocksName = blockManager.getBlocksName();
                Set<String> validBlocksName = blockManager.getBlocks(world).stream().map(ArcheologyBlock::getName).collect(Collectors.toSet());
                sender.sendMessage(Messages.WORLD_HEAD.getMessage("world-name", world.getName()));
                blocksName.forEach(block -> {
                    if (validBlocksName.contains(block)) {
                        sender.sendMessage(Messages.WORLD_DETAIL.getMessage("block-id", block, "state", ChatColor.GREEN + "on"));
                    } else {
                        sender.sendMessage(Messages.WORLD_DETAIL.getMessage("block-id", block, "state", ChatColor.RED + "off"));
                    }
                });
            } else {
                sender.sendMessage(Messages.ERROR_WORLD_NOT_FOUND.getMessageWithPrefix());
            }
        } else {

            sender.sendMessage(Messages.ERROR_BLOCK_NOT_FOUND.getMessageWithPrefix());
        }
    }
}
