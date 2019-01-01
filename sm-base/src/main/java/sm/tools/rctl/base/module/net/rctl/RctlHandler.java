package sm.tools.rctl.base.module.net.rctl;

import sm.tools.rctl.base.module.net.proto.Message;

import java.io.IOException;

public interface RctlHandler<T> {
    void handle(RctlChannel channel, Message<T> message) throws IOException;
}
