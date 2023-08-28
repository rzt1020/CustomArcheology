package cn.myrealm.customarcheology.commands.subcommands;

import cn.myrealm.customarcheology.commands.MainCommand;
import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.util.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt10
 */
public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get descriptions of all commands";
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
    public List<String> onTabComplete(int argsNum) {
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!Permissions.HELP.hasPermission(sender)) {
            return;
        }
        sender.sendMessage("------CustomArcheology Help------");
        for (SubCommand subCommand : MainCommand.SUB_COMMANDS.values()) {
            sender.sendMessage(subCommand.getUsage() + " - " + subCommand.getDescription());
        }
    }
}
