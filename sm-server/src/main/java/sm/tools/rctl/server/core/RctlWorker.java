package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.utils.IOUtil;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Socket;

@SuppressWarnings("unchecked")
public class RctlWorker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RctlWorker.class);
    private Socket socket;

    public RctlWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String thread = Thread.currentThread().getName();
        String clientHost = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();

        logger.info("[{}] accepted [{}:{}]", thread, clientHost, clientPort);
        try {
            InputStream inputStream = socket.getInputStream();
            Message<?> message = new MessageResolver(inputStream).resolve();
            String action = message.getHeader().getAction();
            Method handler = MemoryCache.get(RctlConstants.CACHE_KEY_HANDLER, action);
            handler.invoke(RctlServer.getHandler(), socket, message);
        } catch (Exception e) {
            logger.error(String.format("[%s]处理请求发生错误", thread), e);
        } finally {
            IOUtil.closeQuietly(socket);
        }

    }
}
