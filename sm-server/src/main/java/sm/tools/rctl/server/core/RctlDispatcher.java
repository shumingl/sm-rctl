package sm.tools.rctl.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.base.utils.IOUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RctlDispatcher extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(RctlDispatcher.class);
    private static final String DEFAULT_ENCODE = "UTF-8";
    private static final long READ_TIMEOUT = 1000;
    private static final Map<String, Delegate> delegates = new ConcurrentHashMap<>();

    private Socket socket;

    public RctlDispatcher(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String thread = Thread.currentThread().getName();
        String clientHost = socket.getInetAddress().getHostAddress();
        int clientPort = socket.getPort();

        logger.info("[%s] acpt [%s:%s]", thread, clientHost, clientPort);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            final int MAX_WAIT = 100;
            int count = 0;
            while (true) {

                // ========检查是否有数据接收========
                if (inputStream.available() <= 0) {
                    logger.info("[{}] wait     {}:{}", thread, clientHost, clientPort);
                    if (count >= MAX_WAIT) { // 无请求则跳出
                        break;
                    } else {
                        count++;
                        Thread.sleep(100);
                        continue;
                    }
                }
                // ========从客户机读取数据========
                MessageResolver resolver = new MessageResolver(inputStream, RctlConstants.CHARSET_UTF8);
                byte[] bytes = resolver.getBytes();
                Header header = resolver.getHeader();

                String info = String.format("%s(%s/%s)", header.getId(), header.getIndex() + 1, header.getTotal());
                logger.info("[{}] receive  {}: length={}", thread, info, bytes.length);

                // ========查询路由映射信息========
                logger.info("[{}] router   {}: length={}", thread, info, bytes.length);

                // ========数据转发到远程机========
                logger.info("[{}] dispatch {}: length={}", thread, info, bytes.length);

                // ========从远程机读取应答========
                logger.info("[{}] return   {}: length={}", thread, info, bytes.length);

                // ========应答返回给客户机========
                logger.info("[{}] response {}: {}", thread, clientHost, clientPort);
                outputStream.write(bytes);
                outputStream.flush();
            }
        } catch (Exception e) {
            logger.error(String.format("[%s]处理请求发生错误", thread), e);
        } finally {
            logger.info("[{}] exit.", thread);
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(outputStream);
            IOUtil.closeQuietly(socket);
        }

    }
}
