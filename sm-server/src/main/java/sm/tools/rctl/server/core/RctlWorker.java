package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

        logger.info("[{}] accept [{}:{}]", thread, clientHost, clientPort);
        try {
            InputStream inputStream = socket.getInputStream();
            MessageResolver<?> resolver = new MessageResolver(inputStream);

            Header header = resolver.resolveHeader();
            String action = header.getAction();
            Method handler = MemoryCache.get(RctlConstants.CACHE_KEY_SERVER_HANDLER, action);

            logger.info("[{}] handle [{}->{}]", thread, action, handler);
            handler.invoke(RctlServer.getHandler(), socket,
                    new Message<>(header, resolver.resolveBody(getBodyType(action))));

        } catch (Exception e) {
            logger.error(String.format("[%s]处理请求发生错误", thread), e);
        } finally {
            IOUtils.closeQuietly(socket);
        }

    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getBodyType(String action) throws ClassNotFoundException {
        Method handler = MemoryCache.get(RctlConstants.CACHE_KEY_SERVER_HANDLER, action);
        Type[] types = handler.getGenericParameterTypes();
        String bodyTypeName = ((ParameterizedType) types[1]).getActualTypeArguments()[0].getTypeName();
        return (Class<T>) Class.forName(bodyTypeName);
    }

}
