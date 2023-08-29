package cn.myrealm.customarcheology.commands.subcommands;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.utils.enums.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt10
 */
public class ReloadCommand implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_RELOAD.getMessage();
    }

    @Override
    public String getUsage() {
        return "/customarcheology reload";
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
        CustomArcheology.plugin.reloadPlugin();
    }
}
