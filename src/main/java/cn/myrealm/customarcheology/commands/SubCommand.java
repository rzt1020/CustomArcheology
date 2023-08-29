package cn.myrealm.customarcheology.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author rzt10
 */
public interface SubCommand {

    String getName();
    String getDescription();
    String getUsage();
    List<String> getSubCommandAliases();

    List<String> onTabComplete(int argsNum);
    void execute(CommandSender sender, String[] args) throws Exception;
}
