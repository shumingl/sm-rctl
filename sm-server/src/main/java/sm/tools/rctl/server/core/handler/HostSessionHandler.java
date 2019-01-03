package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage.RESULT;
import sm.tools.rctl.base.module.net.proto.body.HostSession;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.server.router.SessionRouterTable;
import sm.tools.rctl.base.module.net.rctl.RctlSession;

import java.io.IOException;

@ActionHandler("session")
public class HostSessionHandler implements RctlHandler<HostSession> {
    private static final Logger logger = LoggerFactory.getLogger(HostSessionHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<HostSession> message) throws IOException {

        Header header = message.getHeader();
        HostSession establish = message.getBody();

        try {
            String sessionId = establish.getSession();
            logger.info("[REMOTE]远程机响应：{}", sessionId);
            // 登记远程机Socket
            RctlSession session = new RctlSession(sessionId, null, channel);
            SessionRouterTable.merge(session); // 更新会话信息
            logger.info("[REMOTE]客户机通道：{}", SessionRouterTable.getClient(sessionId));
            logger.info("[REMOTE]远程机通道：{}", SessionRouterTable.getRemote(sessionId));
            ReturnMessage retMsg = new ReturnMessage(RESULT.SUCCEED, "[REMOTE]会话创建成功：" + establish.getSession());
            channel.write(new Message<>(header, retMsg));

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("建立会话异常", e);
        }

    }
}
