package cn.myrealm.customarcheology.commands;

import cn.myrealm.customarcheology.util.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rzt10
 */
public class MainCommand implements CommandExecutor, TabCompleter {

    public static final Map<String, SubCommand> SUB_COMMANDS = new HashMap<>();

    public void registerSubCommand(SubCommand subCommand) {
        SUB_COMMANDS.put(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getSubCommandAliases()) {
            SUB_COMMANDS.put(alias.toLowerCase(), subCommand);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,@NonNull Command command,@NonNull String label, String[] args) {
        if (args.length == 0 && Permissions.HELP.hasPermission(sender)) {
            try {
                SUB_COMMANDS.get("help").execute(sender, args);
            } catch (Exception e) {
                sender.sendMessage("Error executing command: " + e.getMessage());
            }
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        if (SUB_COMMANDS.containsKey(subCommandName) && sender.hasPermission(Permissions.COMMAND + subCommandName)) {
            SubCommand subCommand = SUB_COMMANDS.get(subCommandName);
            try {
                subCommand.execute(sender, args);
            } catch (Exception e) {
                sender.sendMessage("Error executing command: " + e.getMessage());
            }
            return true;
        }

        if (Permissions.HELP.hasPermission(sender)) {
            sender.sendMessage("Unknown subcommand. Type \"/customarcheology help\" for a list of commands.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (String subCommandName : SUB_COMMANDS.keySet()) {
                if (subCommandName.startsWith(args[0].toLowerCase()) && sender.hasPermission("customarcheology." + subCommandName)) {
                    suggestions.add(subCommandName);
                }
            }
        } else if (args.length > 1) {
            if (SUB_COMMANDS.containsKey(args[0].toLowerCase()) && sender.hasPermission(Permissions.COMMAND + args[0].toLowerCase())){
                suggestions.addAll(SUB_COMMANDS.get(args[0].toLowerCase()).onTabComplete(args.length - 1));
            }
        }
        return suggestions;
    }
}
