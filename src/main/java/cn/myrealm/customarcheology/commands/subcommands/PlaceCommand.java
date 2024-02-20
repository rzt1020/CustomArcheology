package cn.myrealm.customarcheology.commands.subcommands;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author rzt1020
 */
public class PlaceCommand implements SubCommand {
    @Override
    public String getName() {
        return "place";
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_PLACE.getMessage();
    }

    @Override
    public String getUsage() {
        return "/customarcheology place <blockID> <world> <x> <y> <z>";
    }

    @Override
    public List<String> getSubCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public List<String> onTabComplete(int argsNum, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (argsNum == FIRST_ARGUMENT) {
            BlockManager blockManager = BlockManager.getInstance();
            suggestions.addAll(blockManager.getBlocksName());
        } else if (argsNum == SECOND_ARGUMENT) {
            suggestions.addAll(Bukkit.getWorlds().stream().map(World::getName).toList());
        } else if (argsNum == THIRD_ARGUMENT) {
            suggestions = List.of("[x]");
        } else if (argsNum == FOURTH_ARGUMENT) {
            suggestions = List.of("[y]");
        } else if (argsNum == FIRTH_ARGUMENT) {
            suggestions = List.of("[z]");
        }
        return suggestions;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!Permissions.COMMAND_PLACE.hasPermission(sender)) {
            return;
        }

        if (args.length != FIVE_ARGUMENTS) {
            sender.sendMessage(Messages.ERROR_INCORRECT_COMMAND.getMessageWithPrefix());
            return;
        }

        BlockManager blockManager = BlockManager.getInstance();

        if (!blockManager.isBlockExists(args[FIRST_ARGUMENT])) {
            sender.sendMessage(Messages.ERROR_BLOCK_NOT_FOUND.getMessageWithPrefix("block-id", args[THIRD_ARGUMENT]));
            return;
        }

        World world = Bukkit.getWorld(args[SECOND_ARGUMENT]);

        if (Objects.isNull(world)) {
            sender.sendMessage(Messages.ERROR_WORLD_NOT_FOUND.getMessageWithPrefix());
            return;
        }

        Location placeLocation = new Location(world, Integer.parseInt(args[THIRD_ARGUMENT]), Integer.parseInt(args[FOURTH_ARGUMENT]),
                Integer.parseInt(args[FIRTH_ARGUMENT]));

        placeLocation.getBlock().setType(Material.AIR);

        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> {
            blockManager.placeBlock(blockManager.getBlock(args[FIRST_ARGUMENT]), placeLocation);
        }, 1);

        sender.sendMessage(Messages.GAME_PLACE.getMessageWithPrefix("block-id", args[FIRST_ARGUMENT]
                , "world", args[SECOND_ARGUMENT], "pos", args[THIRD_ARGUMENT] + ", " + args[FOURTH_ARGUMENT] + ", " + args[FIRTH_ARGUMENT]));
    }
}
