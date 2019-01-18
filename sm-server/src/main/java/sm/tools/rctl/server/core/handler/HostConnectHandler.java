package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.constant.RctlActions;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.RespMsg;
import sm.tools.rctl.base.module.net.proto.body.RespMsg.RESULT;
import sm.tools.rctl.base.module.net.proto.body.HostConnect;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.base.module.net.rctl.RctlSession;
import sm.tools.rctl.base.utils.IOUtils;
import sm.tools.rctl.server.core.RctlRequestQueue;
import sm.tools.rctl.server.core.router.SessionRouterTable;

import java.io.IOException;

@ActionHandler(RctlActions.CLIENT_CONNECT)
public class HostConnectHandler implements RctlHandler<HostConnect> {
    private static final Logger logger = LoggerFactory.getLogger(HostConnectHandler.class);
    private static final long DEFAULT_TIMEOUT = 30000L;

    @Override
    public void handle(RctlChannel channel, Message<HostConnect> message) throws IOException {

        Header header = message.getHeader();
        HostConnect request = message.getBody();

        try {
            // 客户机请求建立会话
            RctlSession session = new RctlSession(channel, null);
            String sessionId = session.getSession();
            String from = request.getFrom();
            String target = request.getTarget();

            logger.info("登记会话：[{}]{}->{}", sessionId, from, target);
            RctlRequestQueue.register(target, request.withSession(sessionId));

            SessionRouterTable.put(session); // 登记客户机

            long timeout = request.getTimeout();
            if (timeout <= 0) timeout = DEFAULT_TIMEOUT;

            boolean isTimeout = false;
            long start = System.currentTimeMillis();
            // 等待远程机
            while (!SessionRouterTable.hasRemote(sessionId)) {
                isTimeout = System.currentTimeMillis() - start > timeout;
                if (isTimeout) break;
                Thread.sleep(1);
            }

            if (!isTimeout) {
                RctlChannel remote = SessionRouterTable.getRemote(sessionId);
                if (remote == null) {
                    throw new IOException("[CLIENT]创建会话失败：获取远程通道失败");
                } else {
                    logger.info("[CLIENT]连接远程机：" + remote.getRemoteHost() + "，SESSION：" + sessionId);
                    RespMsg respMsg = new RespMsg(RESULT.SUCCEED, "[CLIENT]会话创建成功：" + sessionId);
                    channel.write(new Message<>(header, respMsg));
                    bridge(sessionId);
                }

            } else {
                channel.write(new Message<>(header,
                        new RespMsg(RESULT.FAILED, "[CLIENT]创建会话失败：请求超时 timeout=" + timeout)));
                RctlSession oldSession = SessionRouterTable.remove(sessionId);// 超时了，移除会话
                IOUtils.closeQuietly(oldSession.getRemote()); // 关闭远程机的通信通道
                IOUtils.closeQuietly(oldSession.getClient()); // 客户机的通信通道，就是当前channel
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("[CLIENT]建立会话异常", e);
        }

    }

    /**
     * 通道桥接
     *
     * @param sessionId 会话ID
     * @throws IOException 通道异常
     */
    private void bridge(String sessionId) throws IOException {
        RctlChannel clientChannel = SessionRouterTable.getClient(sessionId);
        RctlChannel remoteChannel = SessionRouterTable.getRemote(sessionId);
        if (clientChannel == null) throw new IOException("客户机会话通道异常");
        if (remoteChannel == null) throw new IOException("远程机会话通道异常");

        new Thread(() -> {
            Thread.currentThread().setName("bridge-forward-" + sessionId);
            while (!clientChannel.isClosed() && !remoteChannel.isClosed()) {
                try {
                    clientChannel.forward(remoteChannel);
                } catch (Exception e) {
                    logger.warn("转发异常", e);
                }
            }
        }).start();

        new Thread(() -> {
            Thread.currentThread().setName("bridge-receive-" + sessionId);
            while (!clientChannel.isClosed() && !remoteChannel.isClosed()) {
                try {
                    clientChannel.receive(remoteChannel);
                } catch (Exception e) {
                    logger.warn("收取异常", e);
                }
            }
        }).start();
    }
}
