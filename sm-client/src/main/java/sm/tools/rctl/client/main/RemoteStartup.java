package sm.tools.rctl.client.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.client.core.annotation.ClientHandlerScanner;
import sm.tools.rctl.client.core.net.RemoteHeartBeatThread;

import java.io.IOException;

public class RemoteStartup {

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        new ClientHandlerScanner("");
        RemoteHeartBeatThread remoteHeartBeatThread = new RemoteHeartBeatThread();
        Thread thread = new Thread(remoteHeartBeatThread);
        thread.start();
    }
}
