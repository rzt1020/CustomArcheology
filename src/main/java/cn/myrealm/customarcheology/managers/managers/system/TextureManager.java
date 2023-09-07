package cn.myrealm.customarcheology.managers.managers.system;

import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.SQLs;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author rzt10
 */
public class TextureManager extends AbstractManager {
    private Map<String, Integer> blockCustommodeldataMap;
    private List<Integer> blockCustommodeldataList;
    public static TextureManager instance;

    public TextureManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static TextureManager getInstance() {
        return instance;
    }

    @Override
    protected void onInit() {
        blockCustommodeldataMap = new HashMap<>(5);
        blockCustommodeldataList = new ArrayList<>();
        DatabaseManager.getInstance().executeAsyncQuery(SQLs.QUERY_BLOCK_TABLE.getSQL(), new DatabaseManager.Callback<Map<String, Object>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> results) {
                for (Map<String, Object> result : results) {
                    Object blockId = result.get("block_id"), custommodeldata = result.get("custommodeldata");
                    if (Objects.nonNull(blockId) && Objects.nonNull(custommodeldata)) {
                        blockCustommodeldataMap.put((String) blockId, (Integer) custommodeldata);
                        blockCustommodeldataList.add((Integer) custommodeldata);
                    }
                }
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ()-> {
            loadTextures();
            outputTextures();
            Bukkit.getScheduler().runTask(plugin, ()-> Bukkit.getConsoleSender().sendMessage(Messages.TEXTURE_PACK_CREATED.getMessageWithPrefix()));
        }, 20);
    }


    public int getCustommodeldata(String blockId) {
        if (blockCustommodeldataMap.containsKey(blockId)) {
            return  blockCustommodeldataMap.get(blockId);
        }
        return -1;
    }
    private void loadTextures() {
        if (!new File(Path.BLOCK_TEXTURE_PATH.toString()).exists() && new File(Path.BLOCK_TEXTURE_PATH.toString()).mkdirs()) {
            return;
        }
        File[] blockTextureFiles = new File(Path.BLOCK_TEXTURE_PATH.toString()).listFiles();
        if (blockTextureFiles == null) {
            return;
        }
        for (File blockTextureFile : blockTextureFiles) {
            if (blockTextureFile.getName().endsWith(".png")) {
                String blockId = blockTextureFile.getName().replace(".png", "");
                if (! blockCustommodeldataMap.containsKey(blockId)) {
                    int custommodeldata = 10000;
                    while (blockCustommodeldataList.contains(custommodeldata)) {
                        custommodeldata ++;
                    }
                    String sql = SQLs.INSERT_BLOCK_TABLE.getSQL(blockId, String.valueOf(custommodeldata));
                    DatabaseManager.getInstance().executeAsyncUpdate(sql);
                    blockCustommodeldataMap.put(blockId, custommodeldata);
                    blockCustommodeldataList.add(custommodeldata);
                }
            }
        }
    }

    private void outputTextures(){
        boolean created;
        created = (new File(Path.PACK_PATH.toString()).exists() || mkdirs(Path.PACK_PATH.toString()));
        created = created && (new File(Path.PACK_MAIN_MODEL_PATH.toString()).exists() || mkdirs(Path.PACK_MAIN_MODEL_PATH.toString()));
        created = created && (new File(Path.PACK_BLOCK_MODEL_PATH.toString()).exists() || mkdirs(Path.PACK_BLOCK_MODEL_PATH.toString()));
        created = created && (new File(Path.PACK_BLOCK_TEXTURE_PATH.toString()).exists() || mkdirs(Path.PACK_BLOCK_TEXTURE_PATH.toString()));

        if(created) {
            Map<Integer, String> overrides = new HashMap<>(5);
            for (String blockId : blockCustommodeldataMap.keySet()) {
                File pic = new File(Path.BLOCK_TEXTURE_PATH.toString() , blockId + ".png");
                if (pic.exists()) {
                    try {
                        Files.copy(pic.toPath(), new File(Path.PACK_BLOCK_TEXTURE_PATH.toString(), blockId + ".png").toPath(),  StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String model = Template.BLOCK_MODEL_TEMPLATE.toString().replace("%blockId%", blockId);
                    try {
                        Files.write(new File(Path.PACK_BLOCK_MODEL_PATH.toString(), blockId + ".json").toPath(), model.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    overrides.put(blockCustommodeldataMap.get(blockId), Template.OVERRIDE_TEMPLATE.toString().replace("%blockId%", blockId).replace("%custommodeldata%", String.valueOf(blockCustommodeldataMap.get(blockId))));
                }
            }
            StringBuilder override = new StringBuilder();
            int i = 10000;
            while (!overrides.isEmpty()) {
                while (!overrides.containsKey(i)) {
                    i++;
                }
                override.append(overrides.get(i)).append(",");
                overrides.remove(i);
                i++;
            }
            if (override.length() > 0) {
                override.delete(override.length() - 1, override.length());
            }
            String model = Template.MAIN_MODEL_TEMPLATE.toString().replace("%overrides%", override.toString());
            try {
                Files.write(new File(Path.PACK_BLOCK_MODEL_PATH.toString(), "blue_dye.json").toPath(), model.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Messages.ERROR_FAILED_TO_CREATE_TEXTURE_PACK.getMessageWithPrefix());
        }
    }

    public boolean isBlockTextureExists(String blockId) {
        return blockCustommodeldataMap.containsKey(blockId);
    }

    public static boolean mkdirs(String path) {
        Stack<File> stack = new Stack<>();
        stack.push(new File(path));
        while (! stack.peek().exists()) {
            stack.push(stack.peek().getParentFile());
        }
        stack.pop();
        boolean success = true;
        while (! stack.isEmpty()) {
            success = success && stack.pop().mkdirs();
        }
        return success;
    }
}

enum Path {
    // packs
    PACK_PATH("plugins/CustomArcheology/pack/"),
    PACK_MAIN_MODEL_PATH(PACK_PATH + "assets/minecraft/models/item/"),
    PACK_BLOCK_MODEL_PATH(PACK_PATH + "assets/customarcheology/models/block/"),
    PACK_BLOCK_TEXTURE_PATH(PACK_PATH + "assets/customarcheology/textures/block/"),
    // texture
    BLOCK_TEXTURE_PATH("plugins/CustomArcheology/textures/blocks/");

    private final String path;
    Path(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}

enum Template{
    // templates
    BLOCK_MODEL_TEMPLATE("{\"parent\":\"block/cube_all\",\"textures\":{\"down\":\"customarcheology:block/%blockId%\",\"east\":\"customarcheology:block/%blockId%\",\"north\":\"customarcheology:block/%blockId%\",\"south\":\"customarcheology:block/%blockId%\",\"up\":\"customarcheology:block/%blockId%\",\"west\":\"customarcheology:block/%blockId%\",\"particle\":\"customarcheology:block/%blockId%\"}}"),
    MAIN_MODEL_TEMPLATE("{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"minecraft:item/blue_dye\"},\"overrides\":[%overrides%]}"),
    OVERRIDE_TEMPLATE("{\"predicate\":{\"custom_model_data\":%custommodeldata%},\"model\":\"customarcheology:block/%blockId%\"}");


    private final String template;
    Template(String template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return template;
    }
}
