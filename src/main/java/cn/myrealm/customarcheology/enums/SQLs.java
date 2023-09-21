package cn.myrealm.customarcheology.enums;

/**
 * @author rzt10
 */

public enum SQLs {
    // create tables
    CREATE_BLOCK_TABLE(
            "CREATE TABLE IF NOT EXISTS ca_block (block_id VARCHAR(255) NOT NULL, custommodeldata INT NOT NULL, PRIMARY KEY (block_id), UNIQUE (custommodeldata));",
            "CREATE TABLE IF NOT EXISTS ca_block (block_id TEXT NOT NULL, custommodeldata INTEGER NOT NULL, PRIMARY KEY (block_id), UNIQUE (custommodeldata));"
    ),
    CREATE_TOOL_TABLE(
            "CREATE TABLE IF NOT EXISTS ca_tool (tool_id VARCHAR(255) NOT NULL, custommodeldata INT NOT NULL, PRIMARY KEY (tool_id), UNIQUE (custommodeldata));",
            "CREATE TABLE IF NOT EXISTS ca_tool (tool_id TEXT NOT NULL, custommodeldata INTEGER NOT NULL, PRIMARY KEY (tool_id), UNIQUE (custommodeldata));"
    ),
    CREATE_WORLD_TABLE(
            "CREATE TABLE IF NOT EXISTS ca_world (world_uuid CHAR(36) NOT NULL, block_id VARCHAR(255) NOT NULL);"
    ),
    // queries
    QUERY_BLOCK_TABLE(
            "SELECT * FROM ca_block;"
    ),
    QUERY_TOOL_TABLE(
            "SELECT * FROM ca_tool;"
    ),
    QUERY_WORLD_TABLE(
            "SELECT * FROM ca_world;"
    ),
    // inserts
    INSERT_BLOCK_TABLE(
            "INSERT INTO ca_block (block_id, custommodeldata) VALUES ('{0}', {1});"
    ),
    INSERT_TOOL_TABLE(
            "INSERT INTO ca_tool (tool_id, custommodeldata) VALUES ('{0}', {1});"
    ),
    INSERT_WORLD_TABLE(
            "INSERT INTO ca_world (world_uuid, block_id) VALUES ('{0}', '{1}');"
    ),
    DELETE_WORLD_TABLE(
            "DELETE FROM ca_world WHERE world_uuid = '{0}' AND block_id = '{1}';"
    );

    private final String mysql, sqlite;
    SQLs(String mysql, String sqlite) {
        this.mysql = mysql;
        this.sqlite = sqlite;
    }
    SQLs(String mysql) {
        this.mysql = mysql;
        this.sqlite = mysql;
    }

    public String getSql(String... args) {
        String sql;
        if (Config.USE_MYSQL.asBoolean()) {
            sql = mysql;
        } else {
            sql = sqlite;
        }
        for (int i = 0; i < args.length; i++) {
            sql = sql.replace("{" + i + "}", args[i]);
        }
        return sql;
    }
}
