package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.server.core.*;
import sm.tools.rctl.server.router.SessionRouterTable;
import sm.tools.rctl.server.router.entity.RctlSession;

import java.io.IOException;

@ActionHandler("establish")
public class SessionEstablishHandler implements RctlHandler<SessionEstablish> {
    private static final Logger logger = LoggerFactory.getLogger(SessionEstablishHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<SessionEstablish> message) throws IOException {

        Header header = message.getHeader();
        SessionEstablish establish = message.getBody();

        long defaultTimeout = 10000L;

        try {
            if (!StringUtil.isNOE(establish.getSession())) { // 远程机响应建立会话

                logger.info("远程响应：{}->{}", establish.getSession());
                // 登记远程机Socket
                RctlSession session = new RctlSession(null, channel);
                SessionRouterTable.merge(session); // 更新会话信息
                ReturnMessage retMsg = new ReturnMessage(ReturnMessage.RESULT.SUCCEED, "连接成功");
                channel.write(new Message<>(header, retMsg));

            } else { // 客户机请求建立会话

                logger.info("请求会话：{}->{}", establish.getFrom(), establish.getTarget());
                SessionQueue.add(establish.getTarget(), establish);
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
                while (remote == null && System.currentTimeMillis() - start < timeout) {
                    remote = SessionRouterTable.getRemote(sessionId);
                    Thread.sleep(1);
                }
                establish.setSession(sessionId);
                channel.write(new Message<>(header, establish));

            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("建立会话异常", e);
        }

    }
}
