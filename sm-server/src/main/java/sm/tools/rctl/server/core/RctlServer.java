package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RctlServer {

    private static final Logger logger = LoggerFactory.getLogger(RctlServer.class);

    private ServerSocket server;
    private ExecutorService pool;
    private int port;
    private volatile boolean stop = false;

    public RctlServer() {
    }

    public void withPort(int port) {
        this.port = port;
    }

    public RctlServer(int port) {
        this.port = port;
    }

    public void startup() throws IOException {
        pool = Executors.newFixedThreadPool(20);
        server = new ServerSocket(port);
        logger.info("server startup: port=" + port);
        while (!stop) {
            try {
                RctlChannel channel = new RctlChannel(server.accept());
                pool.execute(new RctlWorker(channel));
                Thread.sleep(1);
            } catch (Exception e) {
                logger.error("处理请求失败", e);
            }
        }
    }

    public void shutdown() throws IOException {
        stop = true;
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
        server.close();
    }

}
