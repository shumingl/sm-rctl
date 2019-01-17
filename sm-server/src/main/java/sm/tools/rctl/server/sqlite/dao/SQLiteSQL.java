package sm.tools.rctl.server.sqlite.dao;

public class SQLiteSQL {
    public static final String CREATE_TABLE_RCTL_HOST =
            "create table if not exists rctl_host (" +
                    "    id text primary key not null," +
                    "    token text not null," +
                    "    ip text," +
                    "    mac text," +
                    "    nick text" +
                    ")";
}
