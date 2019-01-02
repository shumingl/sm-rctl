package sm.tools.rctl.client.main;

import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;

import java.io.IOException;
import java.util.Scanner;

public class ClientStartup {
    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        // TODO client startup


        Header header = new Header("0000", "session");
        SessionEstablish establish = new SessionEstablish("0000", "0000", "shumingl");
        Message<SessionEstablish> establishMessage = new Message<>(header, establish);
        RctlChannel channel = new RctlChannel("rctl.server.");
        Message<ReturnMessage> responseMessage = channel.send(establishMessage, ReturnMessage.class);

        if (responseMessage.getBody().getResult() == ReturnMessage.RESULT.SUCCEED) {

            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String line = scanner.next();
                    Command cmd = new Command("~", line);
                    header.setSession(responseMessage.getHeader().getSession());
                    Message<CommandResult> cmdResult = channel.send(new Message<>(header, cmd), CommandResult.class);
                    System.out.println(cmdResult.getBody().getStdOutput());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(responseMessage.getBody().getMessage());
        }
    }
}
