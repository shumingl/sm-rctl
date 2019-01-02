package sm.tools.rctl.server.core.handler;

import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.base.module.net.rctl.RctlSession;
import sm.tools.rctl.server.router.SessionRouterTable;

import java.io.IOException;

@ActionHandler("command")
public class CommandHandler implements RctlHandler<Command> {
    @Override
    public void handle(RctlChannel channel, Message<Command> message) throws IOException {
        Header header = message.getHeader();
        String sessionId = header.getSession();
        RctlSession session = SessionRouterTable.getSession(sessionId);
        RctlChannel clientChannel = session.getClient();

    }
}
