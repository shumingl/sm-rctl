package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage.RESULT;
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.base.utils.IOUtils;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.server.core.*;
import sm.tools.rctl.server.router.SessionRouterTable;
import sm.tools.rctl.base.module.net.rctl.RctlSession;

import java.io.IOException;

@ActionHandler("session")
public class SessionEstablishHandler implements RctlHandler<SessionEstablish> {
    private static final Logger logger = LoggerFactory.getLogger(SessionEstablishHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<SessionEstablish> message) throws IOException {

        Header header = message.getHeader();
        SessionEstablish establish = message.getBody();

        long defaultTimeout = 30000L;

        try {
            if (!StringUtil.isNOE(establish.getSession())) { // 远程机响应建立会话

                logger.info("远程响应：{}->{}", establish.getSession());
                // 登记远程机Socket
                RctlSession session = new RctlSession(null, channel);
                SessionRouterTable.merge(session); // 更新会话信息
                ReturnMessage retMsg = new ReturnMessage(ReturnMessage.RESULT.SUCCEED, "会话创建成功");
                channel.write(new Message<>(header, retMsg));

            } else { // 客户机请求建立会话

                logger.info("请求会话：{}->{}", establish.getFrom(), establish.getTarget());
                RctlSessionQueue.add(establish.getTarget(), establish);
                logger.info("登记完成：{}->{}", establish.getFrom(), establish.getTarget());

                RctlSession session = new RctlSession(channel, null);
                SessionRouterTable.put(session);

                // 等待远程机Socket
                RctlChannel remote = null;
                String sessionId = session.getSession();

                long start = System.currentTimeMillis();
                long timeout = establish.getTimeout();
                if (timeout <= 0) timeout = defaultTimeout;

                // 远程机没有发起连接并且没有超时，就等待远程机
                boolean isTimeout = false;
                while (remote == null) {
                    isTimeout = System.currentTimeMillis() - start < timeout;
                    if (isTimeout) break;

                    remote = SessionRouterTable.getRemote(sessionId);
                    Thread.sleep(10);
                }
                if (!isTimeout) {
                    logger.info("远程机已连接：" + remote.getRemoteHost());
                    ReturnMessage returnMessage = new ReturnMessage(RESULT.SUCCEED, "创建会话成功：" + sessionId);
                    channel.write(new Message<>(header, returnMessage));
                } else {
                    ReturnMessage returnMessage = new ReturnMessage(RESULT.FAILED,
                            "创建会话失败：请求超时：timeout=" + timeout);
                    RctlSession oldSession = SessionRouterTable.remove(sessionId);// 超时了，移除会话
                    channel.write(new Message<>(header, returnMessage));
                    IOUtils.closeQuietly(oldSession.getRemote()); // 关闭远程机的通信通道
                    IOUtils.closeQuietly(oldSession.getClient()); // 客户机的通信通道，就是当前channel
                }

            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("建立会话异常", e);
        }

    }
}
