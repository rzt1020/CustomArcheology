package cn.myrealm.customarcheology.hooks.mythicdungeons;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import net.playavalon.mythicdungeons.MythicDungeons;
import net.playavalon.mythicdungeons.api.annotations.DeclaredFunction;
import net.playavalon.mythicdungeons.api.annotations.SavedField;
import net.playavalon.mythicdungeons.api.parents.DungeonFunction;
import net.playavalon.mythicdungeons.api.parents.FunctionCategory;
import net.playavalon.mythicdungeons.api.parents.FunctionTargetType;
import net.playavalon.mythicdungeons.menu.MenuButton;
import net.playavalon.mythicdungeons.menu.menuitems.ChatMenuItem;
import net.playavalon.mythicdungeons.player.MythicPlayer;
import net.playavalon.mythicdungeons.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@DeclaredFunction
public class FunctionPlaceBlock extends DungeonFunction {

    @SavedField
    private String blockID;

    public FunctionPlaceBlock(Map<String, Object> config) {
        super("CustomArcheology", config);
        this.targetType = FunctionTargetType.NONE;
        this.blockID = "example";
        this.setCategory(FunctionCategory.PLAYER);
    }

    public FunctionPlaceBlock() {
        super("CustomArcheology");
        this.targetType = FunctionTargetType.NONE;
        this.blockID = "example";
        this.setCategory(FunctionCategory.PLAYER);
    }

    @Override
    public void runFunction(net.playavalon.mythicdungeons.api.events.dungeon.TriggerFireEvent triggerEvent, List<MythicPlayer> targets) {
        if (this.blockID == null) {
            return;
        }
        BlockManager blockManager = BlockManager.getInstance();

        if (!blockManager.isBlockExists(blockID)) {
            return;
        }

        this.location.getBlock().setType(Material.AIR);

        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> {
            blockManager.placeBlock(blockManager.getBlock(blockID), this.location);
        }, 1);
    }

    @Override
    public MenuButton buildMenuButton() {
        MenuButton functionButton = new MenuButton(Material.SAND);
        functionButton.setDisplayName("&aPlace Archeology Block");
        functionButton.addLore("&ePlace archeology block at this location.");
        return functionButton;
    }


    public void buildHotbarMenu() {
        this.menu.addMenuItem(new ChatMenuItem(){

            @Override
            public void buildButton() {
                this.button = new MenuButton(Material.MAP);
                this.button.setDisplayName("&d&lEdit Block ID");
            }

            @Override
            public void onSelect(Player player) {
                player.sendMessage(Util.colorize(MythicDungeons.debugPrefix + "&eWhat archeology block will be placed here?"));
                player.sendMessage(Util.colorize(MythicDungeons.debugPrefix + "&eCurrent block ID: &6" + FunctionPlaceBlock.this.blockID));
            }

            @Override
            public void onInput(Player player, String configID) {
                FunctionPlaceBlock.this.blockID = configID;
                player.sendMessage(Util.colorize(MythicDungeons.debugPrefix + "&aSet block ID to '&6" + configID + "&a'"));
            }
        });
    }

    public String getBlockID() {
        return this.blockID;
    }

    public void setBlockID(String blockID) {
        this.blockID = blockID;
    }
}
