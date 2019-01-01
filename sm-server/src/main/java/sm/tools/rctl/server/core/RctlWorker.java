package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public class RctlWorker extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RctlWorker.class);
    private RctlChannel channel;

    public RctlWorker(RctlChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {

        String thread = Thread.currentThread().getName();
        String clientHost = channel.getRemoteHost().getHostAddress();
        int clientPort = channel.getRemotePort();

        logger.info("[{}] accept [{}:{}]", thread, clientHost, clientPort);
        try {
            InputStream inputStream = channel.getInput();
            MessageResolver<?> resolver = new MessageResolver(inputStream);

            Header header = resolver.resolveHeader();
            String action = header.getAction();
            RctlHandler handler = MemoryCache.get(RctlConstants.CACHE_KEY_SERVER_HANDLER, action);

            logger.info("[{}] handle [{}->{}]", thread, action, handler);
            handler.handle(channel, new Message<>(header, resolver.resolveBody(getBodyType(action))));

        } catch (Exception e) {
            logger.error(String.format("[%s]处理请求发生错误", thread), e);
        } finally {
            IOUtils.closeQuietly(channel);
        }

    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getBodyType(String action) throws ClassNotFoundException {
        RctlHandler<?> handler = MemoryCache.get(RctlConstants.CACHE_KEY_SERVER_HANDLER, action);
        Type[] types = handler.getClass().getGenericInterfaces();
        String bodyTypeName = ((ParameterizedType) types[0]).getActualTypeArguments()[0].getTypeName();
        return (Class<T>) Class.forName(bodyTypeName);
    }

}
