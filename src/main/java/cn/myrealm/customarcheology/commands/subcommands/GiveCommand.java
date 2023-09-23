package cn.myrealm.customarcheology.commands.subcommands;

import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ToolManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author rzt1020
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
    public List<String> onTabComplete(int argsNum, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (argsNum == FIRST_ARGUMENT) {
            suggestions = List.of(BLOCK, TOOL);
        } else if (argsNum == SECOND_ARGUMENT) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        } else if (argsNum == THIRD_ARGUMENT) {
            if (BLOCK.equals(args[FIRST_ARGUMENT])) {
                BlockManager blockManager = BlockManager.getInstance();
                suggestions.addAll(blockManager.getBlocksName());
            } else if (TOOL.equals(args[FIRST_ARGUMENT])) {
                ToolManager toolManager = ToolManager.getInstance();
                suggestions.addAll(toolManager.getToolsName());
            }
        } else if (argsNum == FOURTH_ARGUMENT) {
            suggestions = List.of("[num]");
        }
        return suggestions;
    }

    private final static String BLOCK = "block",
                                TOOL = "tool";

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!Permissions.GIVE.hasPermission(sender)) {
            return;
        }

        if (args.length != THREE_ARGUMENTS && args.length != FOUR_ARGUMENTS) {
            sender.sendMessage(this.getUsage());
            return;
        }

        int num = (args.length == FOUR_ARGUMENTS) ? Integer.parseInt(args[FOURTH_ARGUMENT]) : 1;
        Player player = Bukkit.getPlayer(args[SECOND_ARGUMENT]);

        if (Objects.isNull(player)) {
            sender.sendMessage(ChatColor.RED + "Player " + args[SECOND_ARGUMENT] + " is not online!");
            return;
        }

        switch (args[FIRST_ARGUMENT]) {
            case BLOCK -> handleBlockCommand(sender, args, num, player);
            case TOOL -> handleToolCommand(sender, args, num, player);
            default -> sender.sendMessage("Invalid argument!");
        }
    }

    private void handleBlockCommand(CommandSender sender, String[] args, int num, Player player) {
        BlockManager blockManager = BlockManager.getInstance();

        if (!blockManager.isBlockExists(args[THIRD_ARGUMENT])) {
            sender.sendMessage(ChatColor.RED + "This block does not exist!");
            return;
        }

        player.getInventory().addItem(blockManager.generateItemStack(args[THIRD_ARGUMENT], num));
        sender.sendMessage(ChatColor.GREEN + "Successfully give " + args[THIRD_ARGUMENT] + " to " + args[SECOND_ARGUMENT]);
    }

    private void handleToolCommand(CommandSender sender, String[] args, int num, Player player) {
        ToolManager toolManager = ToolManager.getInstance();

        if (!toolManager.isToolExists(args[THIRD_ARGUMENT])) {
            sender.sendMessage(ChatColor.RED + "This tool does not exist!");
            return;
        }

        for (int i = 0; i < num; i++) {
            player.getInventory().addItem(toolManager.getTool(args[THIRD_ARGUMENT]).generateItem());
        }

        sender.sendMessage(ChatColor.GREEN + "Successfully give " + args[THIRD_ARGUMENT] + " to " + args[SECOND_ARGUMENT]);
    }

}
