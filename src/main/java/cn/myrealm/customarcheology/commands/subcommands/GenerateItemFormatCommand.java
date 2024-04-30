package cn.myrealm.customarcheology.commands.subcommands;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.commands.SubCommand;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.utils.Item.DebuildItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PQguanfang
 */
public class GenerateItemFormatCommand implements SubCommand {
    @Override
    public String getName() {
        return "generateitemformat";
    }

    @Override
    public String getDescription() {
        return "Generate Item Format";
    }

    @Override
    public String getUsage() {
        return "/customarcheology generateitemformat";
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
        if (sender instanceof Player player) {
            YamlConfiguration itemConfig = new YamlConfiguration();
            DebuildItem.debuildItem(player.getInventory().getItemInMainHand(), itemConfig);
            String yaml = itemConfig.saveToString();
            Bukkit.getScheduler().runTaskAsynchronously(CustomArcheology.plugin, () -> {
                Path path = new File(CustomArcheology.plugin.getDataFolder(), "generated-item-format.yml").toPath();
                try {
                    Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            player.sendMessage(Messages.GENERATED_SUCCESS.getMessageWithPrefix());
        } else {
            sender.sendMessage(Messages.ERROR_INCORRECT_COMMAND.getMessageWithPrefix());
        }
    }
}
