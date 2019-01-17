package sm.tools.rctl.server.sqlite.dao;

import sm.tools.rctl.base.utils.IOUtils;

import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

public class ClobUtil {


    /**
     * CLOB 类型字段值转换成String;
     */
    public static String clob2text(Clob clob) throws SQLException {
        StringBuilder builder = new StringBuilder();
        char buffer[] = new char[512];
        if (clob == null)
            return null;
        Reader reader = clob.getCharacterStream();
        int i;
        try {
            while ((i = reader.read(buffer, 0, 512)) != -1)
                builder.append(new String(buffer, 0, i));
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return builder.toString();
    }

}
