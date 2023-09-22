package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
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
    private final String displayName;
    private final List<String> lore;
    private final String texture;
    private final Map<String, Recipe> recipes;
    private final ItemStack toolItem;

    public ArcheologyTool(FileConfiguration config) {
        this.displayName = config.getString(DISPLAY_NAME, null);
        this.lore = config.getStringList(LORE);
        this.texture = config.getString(TEXTURE, null);
        this.recipes = new HashMap<>();

        int recipeIndex = 1;
        while (Objects.nonNull(config.get(RECIPE_ + recipeIndex))) {
            Recipe recipe = new Recipe(config.getStringList(RECIPE_ + recipeIndex + ".shape"), Objects.requireNonNull(config.getConfigurationSection(RECIPE_ + recipeIndex + ".ingredients")).getValues(false));
            this.recipes.put(RECIPE_ + recipeIndex, recipe);
            recipeIndex ++;
        }
        toolItem = generateItem();
        recipes.values().forEach(recipe -> recipe.register(toolItem));
    }

    public ItemStack generateItem() {
        if (Objects.nonNull(this.toolItem)) {
            return toolItem.clone();
        }
        ItemStack itemStack = new ItemStack(Material.BRUSH);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        itemMeta.setCustomModelData(TextureManager.getInstance().getToolCustommodeldata(texture));
        itemMeta.setDisplayName(LanguageManager.parseColor(displayName));
        itemMeta.setLore(lore.stream()
                .map(line -> LanguageManager.parseColor("&r" + line))
                .collect(Collectors.toList()));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    private static class Recipe {
        private final static int CRAFTING_TABLE_SIZE = 3;
        private final String[][] shape;
        private final Material[] ingredients;

        public Recipe(List<String> shapeList, Map<String, Object> ingredientMap) {
            this.shape = new String[3][3];
            for (int i = 0; i < shapeList.size(); i++) {
                shape[i] = shapeList.get(i).split("");
            }

            this.ingredients = new Material[9];
            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                for (int j = 0; j < CRAFTING_TABLE_SIZE; j++) {
                    String ingredientKey = shape[i][j];
                    String itemIdentifier = (String) ingredientMap.get(ingredientKey);
                    ingredients[i * 3 + j] = ItemUtil.getMaterial(itemIdentifier);
                }
            }
        }

        public void register(ItemStack result) {
            ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(CustomArcheology.plugin, result.getType().name()), result);

            String[] shapeStrArray = new String[3];
            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                shapeStrArray[i] = String.join("", shape[i]);
            }
            shapedRecipe.shape(shapeStrArray);

            for (int i = 0; i < CRAFTING_TABLE_SIZE; i++) {
                for (int j = 0; j < CRAFTING_TABLE_SIZE; j++) {
                    char ingredientKey = shape[i][j].charAt(0);
                    if (ingredientKey != ' ') {
                        shapedRecipe.setIngredient(ingredientKey, new RecipeChoice.MaterialChoice(ingredients[i * 3 + j]));
                    }
                }
            }

            Bukkit.addRecipe(shapedRecipe);
        }
    }



    public static ArcheologyTool loadFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return new ArcheologyTool(config);
    }
}
