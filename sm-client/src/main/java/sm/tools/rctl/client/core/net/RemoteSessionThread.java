package sm.tools.rctl.client.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.*;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;

public class RemoteSessionThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RemoteSessionThread.class);
    private static final String configPrefix = "rctl.server.";
    private RctlChannel channel;
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
            channel = new RctlChannel(configPrefix);
            // 远程机向服务端，对客户机的建立会话请求进行应答，发起对应的建立会话答复
            HostSession establish = new HostSession(session);
            Header header = new Header(id, session, "session");
            Message<ReturnMessage> response = channel.send(new Message<>(header, establish), ReturnMessage.class);
            ReturnMessage returnBody = response.getBody();
            logger.info(response.getBody().getMessage());
            // 等待和客户端输入
            if (returnBody.getResult() == ReturnMessage.RESULT.SUCCEED) {
                while (true) {
                    try {
                        while (!channel.isReadable()) {
                            Thread.sleep(10);
                        }
                        // 通道可读时，读取Command消息
                        Message<Command> cmdMsg = channel.receive(Command.class);
                        Header cmdHeader = cmdMsg.getHeader();
                        Command cmdBody = cmdMsg.getBody();
                        logger.info("收到指令{}:{}", cmdHeader.getSession(), cmdBody.getCommand());
                        // TODO 调用指令执行代理程序
                        // 返回命令的执行结果
                        channel.write(new Message<>(header,
                                CommandResult.SUCCEED("命令执行成功：" + cmdBody.getCommand())));
                    } catch (Exception e) {
                        logger.error("接收指令异常", e);
                    }
                }
            } else {
                logger.error("建立会话失败：" + returnBody.getMessage());
            }

        } catch (Exception e) {
            logger.error("Host Register Error", e);
        }
    }
}
