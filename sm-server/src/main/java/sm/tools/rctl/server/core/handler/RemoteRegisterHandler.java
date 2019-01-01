package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.server.core.RctlChannel;
import sm.tools.rctl.server.core.RctlHandler;
import sm.tools.rctl.server.router.RemoteHostTable;
import sm.tools.rctl.server.router.entity.RctlHost;

import java.io.IOException;

@ActionHandler("register")
public class RemoteRegisterHandler implements RctlHandler<HostRegister> {
    private static final Logger logger = LoggerFactory.getLogger(RemoteRegisterHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<HostRegister> message) throws IOException {

        Header header = message.getHeader();
        HostRegister body = message.getBody();

        logger.info("开始注册：" + body.getId());
        if (RemoteHostTable.exists(body.getId())) {
            channel.write(new Message<>(header, new ReturnMessage(ReturnMessage.RESULT.FAILED, "主机已存在")));
            logger.info("主机已存在：" + body.getId());
        } else {
            RemoteHostTable.put(new RctlHost(body.getId(), body.getToken()));
            channel.write(new Message<>(header, new ReturnMessage(ReturnMessage.RESULT.SUCCEED, "注册成功")));
            logger.info("注册成功：" + body.getId());
        }
    }
}
