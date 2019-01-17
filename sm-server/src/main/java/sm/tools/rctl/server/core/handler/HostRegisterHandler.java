package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.constant.RctlActions;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.server.sqlite.ObjectManager;

import java.io.IOException;

@ActionHandler(RctlActions.REMOTE_REGISTER)
public class HostRegisterHandler implements RctlHandler<HostRegister> {
    private static final Logger logger = LoggerFactory.getLogger(HostRegisterHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<HostRegister> message) throws IOException {
        HostRegister host = message.getBody();
        logger.info("注册开始：" + host);
        ObjectManager.getRctlService().saveHost(host);
        channel.write(message);
        logger.info("注册完成：" + host);
    }
}
