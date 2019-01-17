package sm.tools.rctl.server.sqlite.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteJDBC {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteJDBC.class);

    private static final String driverClassName = "org.sqlite.JDBC";
    public static final String DB_RCTL = "rctl-data.db";
    public static final String DB_LOG = "rctl-log.db";

    static {
        try {
            Class.forName(driverClassName);
        } catch (Exception e) {
            throw new RuntimeException("加载JDBC驱动失败", e);
        }
    }

    public static void exec(String db, String sql) {
        logger.info("execute [{}]: {}", db, sql);
        try (Connection connection = getConnection(db)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                }
            } else {
                throw new SQLException("获取连接失败：" + db);
            }
        } catch (SQLException e) {
            throw new RuntimeException("执行SQL异常：" + db, e);
        }
    }

    public static <T> T queryObject(String db, String sql, Class<T> objectClass) {
        logger.info("queryObject [{}]: {}", db, sql);
        T object = null;
        try {
            try (Connection connection = getConnection(db)) {
                if (connection != null) {
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        if (resultSet.next()) {
                            object = objectClass.newInstance();
                            RowMapUtil.mapRow(resultSet, 0, object);
                        }
                    }
                } else {
                    throw new SQLException("获取连接失败：" + db);
                }
            }
        } catch (Exception e) {
            logger.error("查询数据异常", e);
        }
        return object;
    }

    public static <T> List<T> queryList(String db, String sql, Class<T> objectClass) {
        logger.info("queryList [{}]: {}", db, sql);
        List<T> list = new ArrayList<>();
        try {
            try (Connection connection = getConnection(db)) {
                if (connection != null) {
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        while (resultSet.next()) {
                            T object = objectClass.newInstance();
                            RowMapUtil.mapRow(resultSet, 0, object);
                            list.add(object);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询数据异常", e);
        }
        return list;
    }

    public static void createNewDB(String db) {
        try (Connection connection = getConnection(db)) {
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                logger.info("创建数据库：{}，驱动类型：{}", db, meta.getDriverName());
            } else {
                throw new SQLException("创建数据库失败：" + db);
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建数据库失败：" + db, e);
        }
    }

    public static Connection getConnection(String db) throws SQLException {

        DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap("sqlite." + db + ".");
        String url = "jdbc:sqlite:" + config.getString("file");
        boolean autoCommit = config.getBoolean("autoCommit");
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(autoCommit);

        return connection;
    }

}
