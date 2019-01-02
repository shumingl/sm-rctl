package sm.tools.rctl.remote.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;

import java.io.IOException;

public class ClientStartup {
    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        // TODO client startup
    }
}
