package cn.myrealm.customarcheology.commands.subcommands;

import cn.myrealm.customarcheology.commands.MainCommand;
import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt1020
 */
public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_HELP.getMessage();
    }

    @Override
    public String getUsage() {
        return "/customarcheology help";
    }

    @Override
    public List<String> getSubCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public List<String> onTabComplete(int argsNum, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Permissions.HELP.hasPermission(sender)) {
            return;
        }
        if (args.length != NONE_ARGUMENT) {
            sender.sendMessage(getUsage());
        }
        sender.sendMessage(Messages.COMMAND_HELP_HEAD.getMessage());
        for (SubCommand subCommand : MainCommand.SUB_COMMANDS.values()) {
            sender.sendMessage(Messages.COMMAND_HELP_DETAIL.getMessage("command-usage", subCommand.getUsage(), "command-description", subCommand.getDescription()));
        }
    }
}
