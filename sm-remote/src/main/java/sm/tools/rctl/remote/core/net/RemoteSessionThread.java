package sm.tools.rctl.remote.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.remote.core.client.RctlClient;

public class RemoteSessionThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RemoteSessionThread.class);
    private static final String configPrefix = "rctl.server.";
    private RctlClient client;
    private String session;

    private static final String id = "0000";
    private static final String target = "0000";
    private static final String token = "shumingl";

    public RemoteSessionThread(String session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            client = new RctlClient(configPrefix);
            // 建立会话-应答
            SessionEstablish establish = new SessionEstablish(session);
            Header header = new Header(id, session, "establish");
            Message<ReturnMessage> response = client.send(new Message<>(header, establish), ReturnMessage.class);
            logger.info(response.getBody().getMessage());
            // 等待和客户端输入
            // TODO 会话线程逻辑

        } catch (Exception e) {
            logger.error("Host Register Error", e);
        }
    }
}
