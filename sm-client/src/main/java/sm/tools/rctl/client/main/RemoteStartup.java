package sm.tools.rctl.client.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.client.core.annotation.ClientHandlerScanner;
import sm.tools.rctl.client.core.net.HeartBeatThread;

import java.io.IOException;

public class RemoteStartup {

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        new ClientHandlerScanner("");
        HeartBeatThread heartBeatThread = new HeartBeatThread();
        Thread thread = new Thread(heartBeatThread);
        thread.start();
    }
}
