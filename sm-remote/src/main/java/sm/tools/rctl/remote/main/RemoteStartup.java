package sm.tools.rctl.remote.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.remote.module.net.HeartBeatThread;

import java.io.IOException;

public class RemoteStartup {

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        String localhost = NetworkUtils.getLocalHostAddress().getHostAddress();
        HeartBeatThread heartBeatThread = new HeartBeatThread(localhost, 17991);
        Thread thread = new Thread(heartBeatThread);
        thread.start();
    }
}
