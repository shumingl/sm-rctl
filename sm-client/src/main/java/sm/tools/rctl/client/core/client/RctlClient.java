package sm.tools.rctl.client.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.proto.*;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class RctlClient implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RctlClient.class);

    private Socket socket;

    public RctlClient(String configPrefix) throws IOException {
        DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap(configPrefix);
        this.socket = new Socket(config.getString("host"), config.getInteger("port"));
    }

    public RctlClient(Socket socket) {
        this.socket = socket;
    }

    public <I, O> Message<O> send(Message<I> message, Class<O> returnBodyClass) throws IOException {
        MessageBuilder<I> builder = new MessageBuilder<>(message);
        byte[] bytes = builder.build();

        logger.trace("send    : " + new MessagePrinter(bytes).print());
        socket.getOutputStream().write(bytes);
        socket.getOutputStream().flush();

        MessageResolver<O> resolver = new MessageResolver<>(socket.getInputStream());
        Message<O> response = resolver.resolve(returnBodyClass);
        logger.trace("receive : " + new MessagePrinter(resolver.getDataBytes()).print());

        return response;
    }

    public void exchange() throws Exception {
        while (true) {
            try {
                MessageResolver<?> resolver = new MessageResolver<>(socket.getInputStream());
                Header header = resolver.resolveHeader();
                String action = header.getAction();
                Message<?> message = new Message<>(header, resolver.resolveBody(getBodyType(action)));

            } catch (IOException e) {
            } catch (Exception e) {
            }
        }
    }

    private <T> Class<T> getBodyType(String action) {
        return null;
    }

    public void close() {
        IOUtils.closeQuietly(socket);
    }

}
