package cn.myrealm.customarcheology.managers.managers.system;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.SQLs;
import cn.myrealm.customarcheology.managers.BaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author rzt1020
 */
public class TextureManager extends BaseManager {
    private Map<String, Integer> blockCustommodeldataMap,
                                 toolCustommodeldataMap;
    private List<Integer> blockCustommodeldataList,
                          toolCustommodeldataList;
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
        toolCustommodeldataMap = new HashMap<>(5);
        toolCustommodeldataList = new ArrayList<>();
        DatabaseManager.getInstance().executeAsyncQuery(SQLs.QUERY_BLOCK_TABLE.getSql(), new DatabaseManager.Callback<>() {
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
        DatabaseManager.getInstance().executeAsyncQuery(SQLs.QUERY_TOOL_TABLE.getSql(), new DatabaseManager.Callback<>() {
            @Override
            public void onSuccess(List<Map<String, Object>> results) {
                for (Map<String, Object> result : results) {
                    Object toolId = result.get("tool_id"), custommodeldata = result.get("custommodeldata");
                    if (Objects.nonNull(toolId) && Objects.nonNull(custommodeldata)) {
                        toolCustommodeldataMap.put((String) toolId, (Integer) custommodeldata);
                        toolCustommodeldataList.add((Integer) custommodeldata);
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
            if (Config.AUTO_COPY_RESOURCEPACK_ENABLED.asBoolean()) {
                copyResourcePack();
            }
            Bukkit.getScheduler().runTask(plugin, ()-> Bukkit.getConsoleSender().sendMessage(Messages.TEXTURE_PACK_CREATED.getMessageWithPrefix()));
        }, 40);
    }


    public int getBlockCustommodeldata(String blockId) {
        if (blockCustommodeldataMap.containsKey(blockId)) {
            return  blockCustommodeldataMap.get(blockId);
        }
        return -1;
    }

    public int getToolCustommodeldata(String toolId) {
        if (toolCustommodeldataMap.containsKey(toolId)) {
            return  toolCustommodeldataMap.get(toolId);
        }
        return 0;
    }

    private void loadTextures() {
        if (!new File(Path.BLOCK_TEXTURE_PATH.toString()).exists() && new File(Path.BLOCK_TEXTURE_PATH.toString()).mkdirs()) {
            return;
        }
        File[] blockTextureFiles = new File(Path.BLOCK_TEXTURE_PATH.toString()).listFiles();
        if (Objects.isNull(blockTextureFiles)) {
            return;
        }
        for (File blockTextureFile : blockTextureFiles) {
            if (blockTextureFile.getName().endsWith(".png")) {
                String blockId = blockTextureFile.getName().replace(".png", "");
                if (!blockCustommodeldataMap.containsKey(blockId)) {
                    int custommodeldata = Config.BLOCK_START_CUSTOM_MODEL_DATA.asInt();
                    while (blockCustommodeldataList.contains(custommodeldata)) {
                        custommodeldata ++;
                    }
                    String sql = SQLs.INSERT_BLOCK_TABLE.getSql(blockId, String.valueOf(custommodeldata));
                    DatabaseManager.getInstance().executeAsyncUpdate(sql);
                    blockCustommodeldataMap.put(blockId, custommodeldata);
                    blockCustommodeldataList.add(custommodeldata);
                }
            }
        }

        if (!new File(Path.TOOL_TEXTURE_PATH.toString()).exists() && new File(Path.TOOL_TEXTURE_PATH.toString()).mkdirs()) {
            return;
        }
        File[] toolTextureFiles = new File(Path.TOOL_TEXTURE_PATH.toString()).listFiles();
        if (Objects.isNull(toolTextureFiles)) {
            return;
        }
        for (File toolTextureFile : toolTextureFiles) {
            if (toolTextureFile.getName().endsWith(".png")) {
                String toolId = toolTextureFile.getName().replace(".png", "");
                if (! toolCustommodeldataMap.containsKey(toolId)) {
                    int custommodeldata = Config.TOOL_START_CUSTOM_MODEL_DATA.asInt();
                    while (toolCustommodeldataList.contains(custommodeldata)) {
                        custommodeldata ++;
                    }
                    String sql = SQLs.INSERT_TOOL_TABLE.getSql(toolId, String.valueOf(custommodeldata));
                    DatabaseManager.getInstance().executeAsyncUpdate(sql);
                    toolCustommodeldataMap.put(toolId, custommodeldata);
                    toolCustommodeldataList.add(custommodeldata);
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
        created = created && (new File(Path.PACK_TOOL_MODEL_PATH.toString()).exists() || mkdirs(Path.PACK_TOOL_MODEL_PATH.toString()));
        created = created && (new File(Path.PACK_TOOL_TEXTURE_PATH.toString()).exists() || mkdirs(Path.PACK_TOOL_TEXTURE_PATH.toString()));

        if (created) {
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
                File mcmeta = new File(Path.BLOCK_TEXTURE_PATH.toString() , blockId + ".png.mcmeta");
                if (mcmeta.exists()) {
                    try {
                        Files.copy(mcmeta.toPath(), new File(Path.PACK_BLOCK_TEXTURE_PATH.toString(), blockId + ".png.mcmeta").toPath(),  StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            StringBuilder override = new StringBuilder();
            int i = Config.BLOCK_START_CUSTOM_MODEL_DATA.asInt();
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
            try {
                Files.write(new File(Path.PACK_MAIN_MODEL_PATH.toString(), "blue_dye.json").toPath(), Template.MAIN_MODEL_TEMPLATE.toString().replace("%overrides%", override.toString()).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            overrides.clear();
            for (String toolId : toolCustommodeldataMap.keySet()) {
                File pic = new File(Path.TOOL_TEXTURE_PATH.toString() , toolId + ".png");
                if (pic.exists()) {
                    try {
                        Files.copy(pic.toPath(), new File(Path.PACK_TOOL_TEXTURE_PATH.toString(), toolId + ".png").toPath(),  StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String brushModel = Template.BRUSH_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing0Model = Template.BRUSHING_0_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing1Model = Template.BRUSHING_1_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing2Model = Template.BRUSHING_2_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing3Model = Template.BRUSHING_3_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing4Model = Template.BRUSHING_4_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing5Model = Template.BRUSHING_5_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing6Model = Template.BRUSHING_6_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing7Model = Template.BRUSHING_7_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing8Model = Template.BRUSHING_8_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing9Model = Template.BRUSHING_9_MODEL_TEMPLATE.toString().replace("%toolId%", toolId),
                           brushing10Model = Template.BRUSHING_10_MODEL_TEMPLATE.toString().replace("%toolId%", toolId);
                    try {
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + ".json").toPath(), brushModel.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_0.json").toPath(), brushing0Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_1.json").toPath(), brushing1Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_2.json").toPath(), brushing2Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_3.json").toPath(), brushing3Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_4.json").toPath(), brushing4Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_5.json").toPath(), brushing5Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_6.json").toPath(), brushing6Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_7.json").toPath(), brushing7Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_8.json").toPath(), brushing8Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_9.json").toPath(), brushing9Model.getBytes());
                        Files.write(new File(Path.PACK_TOOL_MODEL_PATH.toString(), toolId + "_10.json").toPath(), brushing10Model.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    overrides.put(toolCustommodeldataMap.get(toolId), Template.OVERRIDE_BRUSH_TEMPLATE.toString().replace("%toolId%", toolId).replace("%custommodeldata%", String.valueOf(toolCustommodeldataMap.get(toolId))));
                }
                File mcmeta = new File(Path.BLOCK_TEXTURE_PATH.toString() , toolId + ".png.mcmeta");
                if (mcmeta.exists()) {
                    try {
                        Files.copy(mcmeta.toPath(), new File(Path.PACK_BLOCK_TEXTURE_PATH.toString(), toolId + ".png.mcmeta").toPath(),  StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            override = new StringBuilder();
            i = Config.TOOL_START_CUSTOM_MODEL_DATA.asInt();
            while (!overrides.isEmpty()) {
                while (!overrides.containsKey(i)) {
                    i++;
                }
                override.append(", ").append(overrides.get(i));
                overrides.remove(i);
                i++;
            }
            try {
                Files.write(new File(Path.PACK_MAIN_MODEL_PATH.toString(), "brush.json").toPath(), Template.MAIN_BRUSH_MODEL_TEMPLATE.toString().replace("%overrides%", override.toString()).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Bukkit.getConsoleSender().sendMessage(Messages.ERROR_FAILED_TO_CREATE_TEXTURE_PACK.getMessageWithPrefix());
        }
    }

    private void copyResourcePack() {
        try {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fCopying your resource pack to "
                    + Config.AUTO_COPY_RESOURCEPACK_PATH.asString() + "!");
            if (!Bukkit.getPluginManager().isPluginEnabled(Config.AUTO_COPY_RESOURCEPACK_PLUGIN.asString())) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: " + Config.AUTO_COPY_RESOURCEPACK_PLUGIN.asString() +
                        " is not installed in your server! Maybe its have errors when loading or you just typo!");
                return;
            }
            mkdirs(Bukkit.getPluginManager().getPlugin(Config.AUTO_COPY_RESOURCEPACK_PLUGIN.asString()).getDataFolder().getPath()
                    + Config.AUTO_COPY_RESOURCEPACK_PATH.asString());
            copyFolder(CustomArcheology.plugin.getDataFolder().getPath() + "/pack/assets/",
                    Bukkit.getPluginManager().getPlugin(Config.AUTO_COPY_RESOURCEPACK_PLUGIN.asString()).getDataFolder().getPath()
                            + Config.AUTO_COPY_RESOURCEPACK_PATH.asString());
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fCopy finished, don't forgot reload " +
                    "your ItemsAdder or Oraxen or other resource pack plugin!");
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not copy resource pack to " +
                    Config.AUTO_COPY_RESOURCEPACK_PLUGIN.asString() + Config.AUTO_COPY_RESOURCEPACK_PATH.asString() + "!");
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

    public static void copyFolder(String sourcePath, String destinationPath) throws IOException {
        File source = new File(sourcePath);
        File destination = new File(destinationPath);
        if (!destination.exists()) {
            destination.mkdirs();
        }
        if (source.exists() && source.isDirectory()) {
            File[] files = source.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String sourceSubDir = sourcePath + "/" + file.getName();
                        String destinationSubDir = destinationPath + "/" + file.getName();
                        copyFolder(sourceSubDir, destinationSubDir);
                    } else {
                        java.nio.file.Path sourceFilePath = file.toPath();
                        java.nio.file.Path destinationFilePath = new File(destinationPath + "/" + file.getName()).toPath();
                        Files.copy(sourceFilePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

}

enum Path {
    // packs
    PACK_PATH("plugins/CustomArcheology/pack/"),
    PACK_MAIN_MODEL_PATH(PACK_PATH + "assets/minecraft/models/item/"),
    PACK_BLOCK_MODEL_PATH(PACK_PATH + "assets/customarcheology/models/block/"),
    PACK_BLOCK_TEXTURE_PATH(PACK_PATH + "assets/customarcheology/textures/block/"),
    PACK_TOOL_MODEL_PATH(PACK_PATH + "assets/customarcheology/models/item/"),
    PACK_TOOL_TEXTURE_PATH(PACK_PATH + "assets/customarcheology/textures/item/"),
    // texture
    BLOCK_TEXTURE_PATH("plugins/CustomArcheology/textures/blocks/"),
    TOOL_TEXTURE_PATH("plugins/CustomArcheology/textures/tools/");

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
    // block templates
    BLOCK_MODEL_TEMPLATE("{\"parent\":\"block/cube_all\",\"textures\":{\"down\":\"customarcheology:block/%blockId%\",\"east\":\"customarcheology:block/%blockId%\",\"north\":\"customarcheology:block/%blockId%\",\"south\":\"customarcheology:block/%blockId%\",\"up\":\"customarcheology:block/%blockId%\",\"west\":\"customarcheology:block/%blockId%\",\"particle\":\"customarcheology:block/%blockId%\"}}"),
    MAIN_MODEL_TEMPLATE("{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"minecraft:item/blue_dye\"},\"overrides\":[%overrides%]}"),
    OVERRIDE_TEMPLATE("{\"predicate\":{\"custom_model_data\":%custommodeldata%},\"model\":\"customarcheology:block/%blockId%\"}"),
    // brush templates
    BRUSH_MODEL_TEMPLATE("{\"parent\":\"item/handheld\",\"textures\":{\"layer0\":\"customarcheology:item/%toolId%\"}}"),
    BRUSHING_0_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 45], \"translation\": [0, 4.75, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -45], \"translation\": [0, 4.75, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_1_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 31.0942], \"translation\": [1.8541, 4.559, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -58.9058], \"translation\": [1.8541, 4.559, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_2_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 18.5497], \"translation\": [3.5267, 4.059, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -71.4503], \"translation\": [3.5267, 4.059, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_3_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 8.5942], \"translation\": [4.8541, 3.441, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -81.4058], \"translation\": [4.8541, 3.441, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_4_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 2.2025], \"translation\": [5.7063, 2.941, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -87.7975], \"translation\": [5.7063, 2.941, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_5_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"translation\": [6, 2.75, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -90], \"translation\": [6, 2.75, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_6_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 49.6535], \"translation\": [-0.5347, 4.7697, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -40.3465], \"translation\": [-0.5347, 4.7697, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_7_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 53.8168], \"translation\": [-1.0922, 4.603, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -36.1832], \"translation\": [-1.0922, 4.603, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_8_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 57.1353], \"translation\": [-1.5347, 4.647, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -32.8647], \"translation\": [-1.5347, 4.647, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_9_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 59.2658], \"translation\": [-1.8188, 4.4803, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -30.7342], \"translation\": [-1.8188, 4.4803, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    BRUSHING_10_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"customarcheology:item/%toolId%\" }, \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 60], \"translation\": [-1.9167, 4.4167, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -30], \"translation\": [-1.9167, 4.4167, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] } } }"),
    MAIN_BRUSH_MODEL_TEMPLATE("{ \"parent\": \"item/generated\", \"textures\": { \"layer0\": \"item/brush\" }, \"overrides\": [ {\"predicate\": {\"brushing\": 0.0}, \"model\": \"item/brush_brushing_0\"}, {\"predicate\": {\"brushing\": 0.05}, \"model\": \"item/brush_brushing_6\"}, {\"predicate\": {\"brushing\": 0.1}, \"model\": \"item/brush_brushing_7\"}, {\"predicate\": {\"brushing\": 0.15}, \"model\": \"item/brush_brushing_8\"}, {\"predicate\": {\"brushing\": 0.2}, \"model\": \"item/brush_brushing_9\"}, {\"predicate\": {\"brushing\": 0.25}, \"model\": \"item/brush_brushing_10\"}, {\"predicate\": {\"brushing\": 0.3}, \"model\": \"item/brush_brushing_9\"}, {\"predicate\": {\"brushing\": 0.35}, \"model\": \"item/brush_brushing_8\"}, {\"predicate\": {\"brushing\": 0.4}, \"model\": \"item/brush_brushing_7\"}, {\"predicate\": {\"brushing\": 0.45}, \"model\": \"item/brush_brushing_6\"}, {\"predicate\": {\"brushing\": 0.5}, \"model\": \"item/brush_brushing_0\"}, {\"predicate\": {\"brushing\": 0.55}, \"model\": \"item/brush_brushing_1\"}, {\"predicate\": {\"brushing\": 0.6}, \"model\": \"item/brush_brushing_2\"}, {\"predicate\": {\"brushing\": 0.65}, \"model\": \"item/brush_brushing_3\"}, {\"predicate\": {\"brushing\": 0.7}, \"model\": \"item/brush_brushing_4\"}, {\"predicate\": {\"brushing\": 0.75}, \"model\": \"item/brush_brushing_5\"}, {\"predicate\": {\"brushing\": 0.8}, \"model\": \"item/brush_brushing_4\"}, {\"predicate\": {\"brushing\": 0.85}, \"model\": \"item/brush_brushing_3\"}, {\"predicate\": {\"brushing\": 0.9}, \"model\": \"item/brush_brushing_2\"}, {\"predicate\": {\"brushing\": 0.95}, \"model\": \"item/brush_brushing_1\"}, {\"predicate\": {\"brushing\": 1.0}, \"model\": \"item/brush_brushing_0\"}%overrides%], \"display\": { \"thirdperson_righthand\": { \"rotation\": [0, 0, 45], \"translation\": [0, 5.25, 0], \"scale\": [0.85, 0.85, 0.85] }, \"thirdperson_lefthand\": { \"rotation\": [0, 0, -45], \"translation\": [0, 5.25, 0], \"scale\": [0.85, 0.85, 0.85] }, \"firstperson_righthand\": { \"rotation\": [0, -90, 25], \"translation\": [1.13, 3.2, 1.13], \"scale\": [0.68, 0.68, 0.68] }, \"firstperson_lefthand\": { \"rotation\": [90, -90, 25], \"translation\": [8, 0.5, -5.5] }, \"ground\": { \"translation\": [0, 2, 0], \"scale\": [0.5, 0.5, 0.5] } } }"),
    OVERRIDE_BRUSH_TEMPLATE("{\"predicate\": {\"brushing\": 0.0, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_0\"}, {\"predicate\": {\"brushing\": 0.05, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_6\"}, {\"predicate\": {\"brushing\": 0.1, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_7\"}, {\"predicate\": {\"brushing\": 0.15, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_8\"}, {\"predicate\": {\"brushing\": 0.2, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_9\"}, {\"predicate\": {\"brushing\": 0.25, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_10\"}, {\"predicate\": {\"brushing\": 0.3, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_9\"}, {\"predicate\": {\"brushing\": 0.35, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_8\"}, {\"predicate\": {\"brushing\": 0.4, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_7\"}, {\"predicate\": {\"brushing\": 0.45, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_6\"}, {\"predicate\": {\"brushing\": 0.5, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_0\"}, {\"predicate\": {\"brushing\": 0.55, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_1\"}, {\"predicate\": {\"brushing\": 0.6, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_2\"}, {\"predicate\": {\"brushing\": 0.65, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_3\"}, {\"predicate\": {\"brushing\": 0.7, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_4\"}, {\"predicate\": {\"brushing\": 0.75, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_5\"}, {\"predicate\": {\"brushing\": 0.8, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_4\"}, {\"predicate\": {\"brushing\": 0.85, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_3\"}, {\"predicate\": {\"brushing\": 0.9, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_2\"}, {\"predicate\": {\"brushing\": 0.95, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_1\"}, {\"predicate\": {\"brushing\": 1.0, \"custom_model_data\":%custommodeldata%}, \"model\": \"customarcheology:item/%toolId%_0\"}");


    private final String template;
    Template(String template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return template;
    }
}
