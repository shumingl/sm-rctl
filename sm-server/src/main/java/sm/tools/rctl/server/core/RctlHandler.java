package sm.tools.rctl.server.core;


import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.server.core.annotation.ActionHandler;
import sm.tools.rctl.server.core.body.HeartBeat;
import sm.tools.rctl.server.core.body.HostRegister;
import sm.tools.rctl.server.core.body.ReturnMessage;
import sm.tools.rctl.server.router.RemoteHostTable;
import sm.tools.rctl.server.router.entity.RemoteHost;

import java.io.OutputStream;
import java.net.Socket;

public class RctlHandler {

    @ActionHandler("register")
    public void remoteRegister(Socket socket, Message<HostRegister> message) {
        Header header = message.getHeader();
        ReturnMessage retMsg = new ReturnMessage();
        try {
            RemoteHost host = new RemoteHost();
            host.setId(header.getId());
            host.setToken(header.getToken());

            RemoteHostTable.put(host);

            retMsg.setResult(0);
            retMsg.setMessage("注册成功");

            Message<ReturnMessage> response = new Message<>(header, retMsg);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(new MessageBuilder<>(response).build());
            outputStream.flush();

        } catch (Exception e) {
        } finally {
        }
    }

    @ActionHandler("beat")
    public void remoteHeartBeat(Socket socket, Message<HeartBeat> message) {
        while (true) {
            try {
            } catch (Exception e) {
            } finally {
            }
        }
    }
}
