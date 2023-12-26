package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.system.TextureManager;
import cn.myrealm.customarcheology.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rzt1020
 */
public class ArcheologyTool {
    private final static String DISPLAY_NAME = "display_name",
                                LORE = "lore",
                                TEXTURE = "texture",
                                RECIPE_ = "recipe_";
    private final String displayName, toolId;
    private final List<String> lore;
    private final String texture;
    private final Map<String, Recipe> recipes;
    private ItemStack toolItem;

    public ArcheologyTool(FileConfiguration config, String toolId) {
        this.toolId = toolId;
        this.displayName = config.getString(DISPLAY_NAME, null);
        this.lore = config.getStringList(LORE);
        this.texture = config.getString(TEXTURE, null);
        this.recipes = new HashMap<>();
        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin,() -> {
            int recipeIndex = 1;
            while (Objects.nonNull(config.get(RECIPE_ + recipeIndex))) {
                Recipe recipe = new Recipe(config.getStringList(RECIPE_ + recipeIndex + ".shape"), Objects.requireNonNull(config.getConfigurationSection(RECIPE_ + recipeIndex + ".ingredients")).getValues(false));
                this.recipes.put(RECIPE_ + recipeIndex, recipe);
                recipeIndex ++;
            }

            toolItem = generateItem();
            recipes.values().forEach(recipe -> recipe.register(toolItem));
        }, 40);
    }

    public ItemStack generateItem() {
        if (Objects.nonNull(this.toolItem)) {
            return toolItem.clone();
        }
        ItemStack itemStack = ItemUtil.generateItemStack(Material.BRUSH, TextureManager.getInstance().getToolCustommodeldata(texture), displayName, lore);
        assert itemStack.getItemMeta() != null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(NamespacedKeys.IS_ARCHEOLOGY_TOOL.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_TOOL_ID.getNamespacedKey(), PersistentDataType.STRING, toolId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void remove() {
        recipes.values().forEach(Recipe::unregister);
    }


    private static class Recipe {
        private final static int CRAFTING_TABLE_SIZE = 3;
        private final String[][] shape;
        private NamespacedKey key;
        private final ItemStack[] ingredients;

        public Recipe(List<String> shapeList, Map<String, Object> ingredientMap) {
            this.shape = new String[3][3];
            for (int i = 0; i < shapeList.size(); i++) {
                shape[i] = shapeList.get(i).split("");
            }

            this.ingredients = new ItemStack[9];
            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                for (int j = 0; j < CRAFTING_TABLE_SIZE; j++) {
                    String ingredientKey = shape[i][j];
                    String itemIdentifier = (String) ingredientMap.get(ingredientKey);
                    if (Objects.nonNull(itemIdentifier)) {
                        ingredients[i * 3 + j] = ItemUtil.getItemStackByItemIdentifier(itemIdentifier);
                    }
                }
            }
        }

        public void register(ItemStack result) {
            UUID randomUid = UUID.randomUUID();
            key = new NamespacedKey(CustomArcheology.plugin, result.getType().name() + "_" + randomUid);
            ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);


            String[] shapeStrArray = new String[3];
            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                shapeStrArray[i] = String.join("", shape[i]);
            }
            shapedRecipe.shape(shapeStrArray);

            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                for (int j = 0; j < CRAFTING_TABLE_SIZE; j++) {
                    char ingredientKey = shape[i][j].charAt(0);
                    if (ingredientKey != ' ') {
                        shapedRecipe.setIngredient(ingredientKey, new RecipeChoice.ExactChoice(ingredients[i * 3 + j]));
                    }
                }
            }

            Bukkit.addRecipe(shapedRecipe);
        }

        public void unregister() {
            Bukkit.removeRecipe(key);
        }
    }

    public static ArcheologyTool loadFromFile(File file, String toolId) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return new ArcheologyTool(config, toolId);
    }
}
