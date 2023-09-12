package cn.myrealm.customarcheology.commands.subcommands;

import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author rzt10
 */
public class GiveCommand implements SubCommand {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_GIVE.getMessage();
    }

    @Override
    public String getUsage() {
        return "/customarcheology give <Player> <BlockID>";
    }

    @Override
    public List<String> getSubCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public List<String> onTabComplete(int argsNum) {
        List<String> suggestions = new ArrayList<>();
        if (argsNum == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (argsNum == 2) {
            BlockManager blockManager = BlockManager.getInstance();
            suggestions.addAll(blockManager.getBlocksName());
        } else if (argsNum == 3) {
            suggestions = Arrays.asList("1", "5", "10", "32", "64");
        }
        return suggestions;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!Permissions.GIVE.hasPermission(sender)) {
            return;
        }
        if (args.length != 4) {
            sender.sendMessage(this.getUsage());
        }
        BlockManager blockManager = BlockManager.getInstance();
        if (blockManager.isBlockExists(args[2])) {
            Player player = Bukkit.getPlayer(args[1]);
            if (Objects.nonNull(player)) {
                player.getInventory().addItem(blockManager.generateItemStack(args[2], Integer.parseInt(args[3])));
                sender.sendMessage(ChatColor.GREEN + "Successfully give " + args[2] + " to " + args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "Player " + args[1] + " is not online!");
            }
        } else {
            sender.sendMessage( ChatColor.RED + "This block does not exist!");
        }
    }
}
