package sm.tools.rctl.server.sqlite;

import org.junit.Before;
import org.junit.Test;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.server.sqlite.dao.SQLiteJDBC;
import sm.tools.rctl.server.sqlite.dao.SQLiteSQL;
import sm.tools.rctl.server.utils.ExpressionParser;

import java.util.List;

public class SQLiteJDBCTest {
    private static final String db = "rctl";

    private static final String SELECT_RCTL_HOST_BYID = "" +
            "select id,token,ip,mac,nick" +
            "  from rctl_host " +
            " where id like '%%%s%%'";

    @Before
    public void setUp() throws Exception {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure("config/logback.xml");
    }

    @Test
    public void exec() {
        SQLiteJDBC.createNewDB(db);
        SQLiteJDBC.exec(db, SQLiteSQL.CREATE_TABLE_RCTL_HOST);

        String insertHostTpl = "insert into rctl_host(id, token, ip ,mac, nick) " +
                "values('${id}','${token}','${ip}','${mac}','${nick}')";
        HostRegister register = new HostRegister()
                .withHost("192.168.1.102", "C8D3FF3D642A")
                .withAuth("0001", "shumingl", "shumingl");

        String insertHostSql = ExpressionParser.getDefault().parse(insertHostTpl, register);
        SQLiteJDBC.exec(db, insertHostSql);
    }

    @Test
    public void queryObject() {
        String sql = String.format(SELECT_RCTL_HOST_BYID, "0000");
        HostRegister register = SQLiteJDBC.queryObject(db, sql, HostRegister.class);
        System.out.println(register);
    }

    @Test
    public void queryList() {
        String sql = String.format(SELECT_RCTL_HOST_BYID, "000");
        List<HostRegister> list = SQLiteJDBC.queryList(db, sql, HostRegister.class);
        for (HostRegister register : list) {
            System.out.println(register);
        }
    }
}