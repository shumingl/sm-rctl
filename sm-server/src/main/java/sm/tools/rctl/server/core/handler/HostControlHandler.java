package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage.RESULT;
import sm.tools.rctl.base.module.net.proto.body.HostConnect;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.base.module.net.rctl.RctlSession;
import sm.tools.rctl.base.utils.IOUtils;
import sm.tools.rctl.server.core.RctlConnectQueue;
import sm.tools.rctl.server.router.SessionRouterTable;

import java.io.IOException;

@ActionHandler("control")
public class HostControlHandler implements RctlHandler<HostConnect> {
    private static final Logger logger = LoggerFactory.getLogger(HostControlHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<HostConnect> message) throws IOException {

        Header header = message.getHeader();
        HostConnect connect = message.getBody();

        long defaultTimeout = 30000L;

        try {
            // 客户机请求建立会话
            RctlSession session = new RctlSession(channel, null);
            String sessionId = session.getSession();

            logger.info("请求会话：[{}]{}->{}", sessionId, connect.getFrom(), connect.getTarget());
            connect.setSession(session.getSession());
            RctlConnectQueue.add(connect.getTarget(), connect);
            logger.info("登记完成：[{}]{}->{}", sessionId, connect.getFrom(), connect.getTarget());

            SessionRouterTable.put(session);

            // 等待远程机Socket
            RctlChannel remote = null;

            long start = System.currentTimeMillis();
            long timeout = connect.getTimeout();
            if (timeout <= 0) timeout = defaultTimeout;

            // 远程机没有发起连接并且没有超时，就等待远程机
            boolean isTimeout = false;
            while (remote == null) {
                isTimeout = System.currentTimeMillis() - start > timeout;
                if (isTimeout) break;

                remote = SessionRouterTable.getRemote(sessionId);
                Thread.sleep(10);
            }
            if (!isTimeout) {
                logger.info("[CLIENT]远程机已连接：" + remote.getRemoteHost() + "，SESSION：" + sessionId);
                ReturnMessage returnMessage = new ReturnMessage(RESULT.SUCCEED, "[CLIENT]会话创建成功：" + sessionId);
                channel.write(new Message<>(header, returnMessage));
                bridging(sessionId);
            } else {
                ReturnMessage returnMessage = new ReturnMessage(RESULT.FAILED,
                        "[CLIENT]创建会话失败：请求超时 timeout=" + timeout);
                RctlSession oldSession = SessionRouterTable.remove(sessionId);// 超时了，移除会话
                channel.write(new Message<>(header, returnMessage));
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
    private void bridging(String sessionId) throws IOException {
        RctlChannel clientChannel = SessionRouterTable.getClient(sessionId);
        RctlChannel remoteChannel = SessionRouterTable.getRemote(sessionId);

        if (clientChannel == null)
            throw new IOException("客户机会话通道异常");
        if (remoteChannel == null)
            throw new IOException("远程机会话通道异常");

        while (true) {
            try {
                clientChannel.write(remoteChannel.send(clientChannel.receive(Command.class), CommandResult.class));
            } catch (Exception e) {
                logger.warn("会话异常", e);
            }
        }
    }
}
