package sm.tools.rctl.base.module.net.rctl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessagePrinter;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.ByteArrayUtils;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RctlChannel implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(RctlChannel.class);

    private Socket socket;
    private long timeout = 30000;
    private final Object lock = new Object();
    private boolean closed;

    public RctlChannel(String configPrefix, long timeout) throws IOException {
        this(configPrefix);
        this.timeout = timeout;
    }

    public RctlChannel(Socket socket, long timeout) {
        this(socket);
        this.timeout = timeout;
    }

    public RctlChannel(String configPrefix) throws IOException {
        DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap(configPrefix);
        this.socket = new Socket(config.getString("host"), config.getInteger("port"));
        closed = false;
    }

    public RctlChannel(Socket socket) {
        this.socket = socket;
        closed = false;
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

    public boolean isReadable() throws IOException {
        return getInput().available() > 0;
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

    public void forward(RctlChannel target) throws IOException {
        target.writeBytes(readBytes()); // 消息转发到target（source->target）
    }

    public void receive(RctlChannel target) throws IOException {
        writeBytes(target.readBytes()); // 从target读取消息（target->source）
    }

    public void writeBytes(byte[] bytes) throws IOException {
        synchronized (lock) {
            socket.getOutputStream().write(bytes);
            socket.getOutputStream().flush();
        }
    }

    /**
     * 读取一条消息的字节数组
     *
     * @return 字节数组
     * @throws IOException 流异常
     */
    public byte[] readBytes() throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] lengthBytes = new byte[RctlConstants.TOTAL_LENGTH_BYTES];
        IOUtils.readFixedBytes(inputStream, lengthBytes, timeout);

        int length = ProtocolUtils.bytes2int(lengthBytes);
        byte[] dataBytes = new byte[length + RctlConstants.TOTAL_LENGTH_BYTES];

        ByteArrayUtils.fill(lengthBytes, dataBytes, 0);// 总长度
        IOUtils.readFixedBytes(inputStream, dataBytes, RctlConstants.TOTAL_LENGTH_BYTES, length, timeout); // 全部内容
        return dataBytes;
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
        byte[] dataBytes = readBytes();
        if (logger.isTraceEnabled())
            logger.trace("receive : " + new MessagePrinter(dataBytes).print());
        return new MessageResolver<I>(dataBytes).resolve(bodyClass);
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
        if (logger.isTraceEnabled())
            logger.trace("write   : " + new MessagePrinter(bytes).print());
        writeBytes(bytes);
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        try {
            socket.close();
        } finally {
            closed = true;
        }
    }
}
