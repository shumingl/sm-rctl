package sm.tools.rctl.base.module.net.rctl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessagePrinter;
import sm.tools.rctl.base.module.net.proto.MessageResolver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RctlChannel implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RctlChannel.class);

    private Socket socket;

    public RctlChannel(String configPrefix) throws IOException {
        DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap(configPrefix);
        this.socket = new Socket(config.getString("host"), config.getInteger("port"));
    }

    public RctlChannel(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInput() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutput() throws IOException {
        return socket.getOutputStream();
    }

    public InetAddress getRemoteHost() {
        return socket.getInetAddress();
    }

    public InetAddress getLocalHost() {
        return socket.getLocalAddress();
    }

    public int getRemotePort() {
        return socket.getPort();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
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

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
