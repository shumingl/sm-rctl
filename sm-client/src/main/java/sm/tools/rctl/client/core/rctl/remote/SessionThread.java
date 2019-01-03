package sm.tools.rctl.client.core.rctl.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.*;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.client.core.agent.cmd.CommandAgent;

public class SessionThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SessionThread.class);
    private static final String configPrefix = "rctl.server.";
    private RctlChannel channel;
    private String session;

    private static final String id = "0000";
    private static final String target = "0000";
    private static final String token = "shumingl";

    public SessionThread(String session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            channel = new RctlChannel(configPrefix);
            // 远程机向服务端，对客户机的建立会话请求进行应答，发起对应的建立会话答复
            HostSession establish = new HostSession(session);
            Header header = new Header(id, session, "session");
            Message<ReturnMessage> response = channel.send(new Message<>(header, establish), ReturnMessage.class);
            ReturnMessage returnBody = response.getBody();
            logger.info(response.getBody().getMessage());
            // 等待和客户端输入
            if (returnBody.getResult() == ReturnMessage.RESULT.SUCCEED) {
                new CommandAgent(channel, header, "cmd.exe").exec();
            } else {
                logger.error("建立会话失败：" + returnBody.getMessage());
            }

        } catch (Exception e) {
            logger.error("Host Register Error", e);
        }
    }
}
