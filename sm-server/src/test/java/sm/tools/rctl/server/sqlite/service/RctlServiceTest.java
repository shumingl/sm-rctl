package sm.tools.rctl.server.sqlite.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.server.sqlite.ObjectManager;

import java.util.List;

import static org.junit.Assert.*;

public class RctlServiceTest {
    private static final RctlService service = ObjectManager.getRctlService();

    @Before
    public void setUp() throws Exception {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure("config/logback.xml");
    }

    @Test
    public void queryHost() {
        HostRegister host = service.queryHost("0001");
        System.out.println(host);
        Assert.assertEquals("HOST: ", "0001", host.getId());
    }

    @Test
    public void queryHostList() {
        List<HostRegister> hosts = service.queryHostList(new HostRegister());
        System.out.println(hosts);
        Assert.assertEquals("HOST: ", "0000", hosts.get(0).getId());
    }

    @Test
    public void saveHost() {
        HostRegister host = new HostRegister()
                .withAuth(null, "password", "shuming.liu")
                .withHost("10.0.19.113", "D8-D3-FF-3D-64-2B");
        service.saveHost(host);
        HostRegister queryHost = service.queryHost(host.getId());
        System.out.println(queryHost);
    }

    @Test
    public void updateHost() {
        HostRegister host = new HostRegister()
                .withAuth("0001", "password", null)
                .withHost("10.0.19.84", "C8-D3-FF-3D-64-2A");
        service.updateHost(host);
        HostRegister queryHost = service.queryHost(host.getId());
        System.out.println(queryHost);
    }
}