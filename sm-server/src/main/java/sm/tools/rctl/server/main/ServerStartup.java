package sm.tools.rctl.server.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.server.core.RctlServer;
import sm.tools.rctl.server.core.annotation.ServerHandlerScanner;

import java.io.IOException;

public class ServerStartup {
    private static RctlServer server = new RctlServer(17991);

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        new ServerHandlerScanner("sm.tools.rctl.server.core.handler");
        server.startup();
    }
}
