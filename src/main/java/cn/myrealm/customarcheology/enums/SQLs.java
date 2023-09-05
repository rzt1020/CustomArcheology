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
    // queries
    QUERY_BLOCK_TABLE(
            "SELECT * FROM ca_block;"
    ),
    // inserts
    INSERT_BLOCK_TABLE(
            "INSERT INTO ca_block (block_id, custommodeldata) VALUES ('{0}', {1});"
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

    public String getSQL(String... args) {
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
