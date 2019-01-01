package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessagePrinter;
import sm.tools.rctl.base.module.net.proto.MessageResolver;

import java.io.IOException;
import java.net.Socket;

public class RctlChannel {

    private static final Logger logger = LoggerFactory.getLogger(RctlChannel.class);

    private Socket socket;

    public RctlChannel(Socket socket) {
        this.socket = socket;
    }

    /**
     * 发送并接受返回消息
     *
     * @param message   待发送的消息
     * @param bodyClass 返回消息body的class
     * @param <I>       要读取的类型
     * @param <O>       要发送的类型
     * @return
     * @throws IOException
     */
    public <I, O> Message<I> send(Message<O> message, Class<I> bodyClass) throws IOException {
        write(message);
        return receive(bodyClass);
    }

    /**
     * 收取消息
     *
     * @param bodyClass 消息body的class
     * @param <I>       要读取的类型
     * @return 收到的消息
     * @throws IOException 流异常
     */
    public <I> Message<I> receive(Class<I> bodyClass) throws IOException {

        MessageResolver<I> resolver = new MessageResolver<>(socket.getInputStream());
        Message<I> response = resolver.resolve(bodyClass);
        logger.trace("receive : " + new MessagePrinter(resolver.getDataBytes()).print());

        return response;
    }

    /**
     * 发送消息
     *
     * @param message 待发送的消息
     * @param <O>     要发送的类型
     * @throws IOException 流异常
     */
    public <O> void write(Message<O> message) throws IOException {

        MessageBuilder<O> builder = new MessageBuilder<>(message);
        byte[] bytes = builder.build();

        logger.trace("write   : " + new MessagePrinter(bytes).print());
        socket.getOutputStream().write(bytes);
        socket.getOutputStream().flush();

    }

}
