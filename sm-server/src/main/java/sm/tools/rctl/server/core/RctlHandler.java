package sm.tools.rctl.server.core;


import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.server.core.annotation.ActionHandler;
import sm.tools.rctl.server.core.body.HostRegister;

import java.net.Socket;

public class RctlHandler {

    @ActionHandler("register")
    public void remoteRegister(Socket socket, Message<HostRegister> message) {
        try {
        } catch (Exception e) {
        } finally {
        }
    }

    @ActionHandler("beat")
    public void remoteHeartBeat(Socket socket, Message<HostRegister> message) {
        while (true) {
            try {
            } catch (Exception e) {
            } finally {
            }
        }
    }
}
