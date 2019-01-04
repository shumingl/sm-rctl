package sm.tools.rctl.remote.core.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.*;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.remote.core.agent.program.ProgramAgent;
import sm.tools.rctl.remote.core.annotation.ActionCallback;

@ActionCallback("program")
public class ProgramThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ProgramThread.class);
    private static final String serverConfigPrefix = "rctl.server.";
    private static final String remoteConfigPrefix = "rctl.remote.";
    private String session;

    public ProgramThread(String session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap(remoteConfigPrefix);
            RctlChannel channel = new RctlChannel(serverConfigPrefix);
            // 远程机向服务端，对客户机的建立会话请求进行应答，发起对应的建立会话答复
            HostSession establish = new HostSession(session);
            Header header = new Header(config.getString("id"), session, "session");
            Message<ReturnMessage> response = channel.send(new Message<>(header, establish), ReturnMessage.class);
            ReturnMessage returnBody = response.getBody();
            logger.info(response.getBody().getMessage());
            // 等待和客户端输入
            if (returnBody.getResult() == ReturnMessage.RESULT.SUCCEED) {
                new ProgramAgent(channel, session, "powershell.exe").exec();
            } else {
                logger.error("建立会话失败：" + returnBody.getMessage());
            }
        } catch (Exception e) {
            logger.error("Host Register Error", e);
        }
    }
}
