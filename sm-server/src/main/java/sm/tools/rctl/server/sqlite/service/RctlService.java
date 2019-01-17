package sm.tools.rctl.server.sqlite.service;

import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.server.sqlite.dao.IDGenerator;
import sm.tools.rctl.server.sqlite.dao.SQLiteJDBC;
import sm.tools.rctl.server.sqlite.dao.SqlBuilder;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RctlService {

    private static final String db = "rctl";

    /**
     * 控制Host ID的自增锁，防止重复ID出现
     */
    private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public HostRegister queryHost(String id) {
        String sql = "select id,token,ip,mac,nick from rctl_host where id = '${id}'";
        SqlBuilder builder = new SqlBuilder(sql, "id", id);
        return SQLiteJDBC.queryObject(db, builder.build(), HostRegister.class);
    }

    public List<HostRegister> queryHostList(HostRegister host) {
        String sql = new SqlBuilder("select id,token,ip,mac,nick from rctl_host")
                .where(host)
                .arguments(host)
                .build();
        return SQLiteJDBC.queryList(db, sql, HostRegister.class);
    }

    public String queryMaxId() {
        String sql = "select max(id) id from rctl_host";
        try {
            readLock.lock();
            HostRegister host = SQLiteJDBC.queryObject(db, sql, HostRegister.class);
            if (host != null)
                return host.getId();
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public void saveHost(HostRegister host) {
        if (host != null) {
            if (StringUtil.isNOE(host.getId())) {

                host.setId(IDGenerator.next());
                String sql = "insert into rctl_host(id, token, ip, mac, nick) " +
                        "values ('${id}', '${token}', '${ip}', '${mac}', '${nick}')";
                SqlBuilder builder = new SqlBuilder(sql, host);
                try {
                    writeLock.lock();
                    SQLiteJDBC.exec(db, builder.build());
                } finally {
                    writeLock.unlock();
                }
            } else {
                updateHost(host);
            }
        }

    }

    public void updateHost(HostRegister host) {
        if (host != null && !StringUtil.isNOE(host.getId())) {
            String sql = "update rctl_host ";
            SqlBuilder builder = new SqlBuilder(sql)
                    .set(host)
                    .where(new HostRegister(host.getId()))
                    .arguments(host);
            try {
                writeLock.lock();
                SQLiteJDBC.exec(db, builder.build());
            } finally {
                writeLock.unlock();
            }
        }
    }
}
