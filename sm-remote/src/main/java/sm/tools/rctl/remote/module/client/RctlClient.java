package sm.tools.rctl.remote.module.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessagePrinter;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class RctlClient implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RctlClient.class);

    private Socket socket;

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
        logger.trace("receive : " + new MessagePrinter(resolver.getBytes()).print());

        return response;
    }

    public void close() {
        IOUtil.closeQuietly(socket);
    }

}
