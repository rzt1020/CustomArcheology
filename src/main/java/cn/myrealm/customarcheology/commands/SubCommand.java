package cn.myrealm.customarcheology.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author rzt10
 */
public interface SubCommand {

    /**
     * get sub command name
     * @return String
     */
    String getName();

    /**
     * get sub command description
     * @return String
     */

    String getDescription();

    /**
     * get sub command usage
     * @return String
     */
    String getUsage();

    /**
     * get sub command aliases
     * @return List<String>
     */
    List<String> getSubCommandAliases();

    /**
     * get tab complete of sub command
     * @param argsNum size of args
     * @return List<String>
     */
    List<String> onTabComplete(int argsNum);

    /**
     * execute sub command
     * @param sender command sender
     * @param args arguments
     * @throws Exception error
     */
    void execute(CommandSender sender, String[] args) throws Exception;
}
