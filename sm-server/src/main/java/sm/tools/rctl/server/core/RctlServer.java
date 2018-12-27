package sm.tools.rctl.server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RctlServer {
    private ServerSocket server;
    private ExecutorService pool;
    private int port;

    public RctlServer(int port) {
        this.port = port;
    }

    public void startup() throws IOException {
        pool = Executors.newFixedThreadPool(20);
        server = new ServerSocket(port);
    }
}
