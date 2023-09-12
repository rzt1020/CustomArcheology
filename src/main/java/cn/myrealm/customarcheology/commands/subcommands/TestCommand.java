package cn.myrealm.customarcheology.commands.subcommands;


import cn.myrealm.customarcheology.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt10
 */
public class TestCommand implements SubCommand {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public String getUsage() {
        return "test";
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
        Player player = (Player) sender;
        if (args.length > 1) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setCustomModelData(Integer.parseInt(args[1]));
            }
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItemInMainHand(itemStack);
            return;
        }
        ItemDisplay display = (ItemDisplay) player.getWorld().spawnEntity(player.getLocation().getBlock().getLocation().add(0.5,0.5,0.5), EntityType.ITEM_DISPLAY);
        display.setItemStack(player.getInventory().getItemInMainHand());
    }
}
