package sm.tools.rctl.server.sqlite.dao;

import sm.tools.rctl.base.module.cache.DynamicEntity;
import sm.tools.rctl.base.utils.ObjectUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class RowMapUtil {

    public static <T> void mapRow(ResultSet rs, int rowNum, T object) throws SQLException {
        DynamicEntity entity = new DynamicEntity(object);
        try {

            ResultSetMetaData meta = rs.getMetaData();
            int total = meta.getColumnCount();
            for (int i = 0; i < total; i++) {
                String columnName = meta.getColumnName(i + 1);
                int columnType = meta.getColumnType(i + 1);
                Object dbValue = rs.getObject(columnName);

                switch (columnType) {
                    case Types.INTEGER:
                        entity.set(columnName, ObjectUtil.getInteger(dbValue));
                        break;
                    case Types.FLOAT:
                        entity.set(columnName, ObjectUtil.getFloat(dbValue));
                        break;
                    case Types.DOUBLE:
                        entity.set(columnName, ObjectUtil.getDouble(dbValue));
                        break;
                    case Types.DECIMAL:
                        entity.set(columnName, ObjectUtil.getDecimal(dbValue));
                        break;
                    case Types.BOOLEAN:
                        entity.set(columnName, ObjectUtil.getBoolean(dbValue));
                        break;
                    case Types.NUMERIC:
                        entity.set(columnName, ObjectUtil.getNumber(dbValue));
                        break;
                    case Types.TIME:
                    case Types.TIMESTAMP:
                    case Types.DATE:
                        entity.set(columnName, rs.getTimestamp(columnName));
                        break;
                    case Types.CLOB:
                        entity.set(columnName, ClobUtil.clob2text(rs.getClob(columnName)));
                        break;
                    case Types.VARCHAR:
                        entity.set(columnName, rs.getString(columnName));
                        break;
                    default:
                        entity.set(columnName, rs.getString(columnName));
                }
            }
        } catch (Exception e) {
            throw new SQLException("JDBC映射类异常：" + object.getClass().getSimpleName(), e);
        }
    }
}
