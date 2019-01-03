package sm.tools.rctl.client.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.HostConnect;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;

import java.io.IOException;
import java.util.Scanner;

public class ClientStartup {

    private static final Logger logger = LoggerFactory.getLogger(ClientStartup.class);

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));
        // TODO client startup


        Header header = new Header("0000", "control");
        HostConnect establish = new HostConnect("0000", "0000", "shumingl");
        Message<HostConnect> establishMessage = new Message<>(header, establish);
        RctlChannel channel = new RctlChannel("rctl.server.");
        Message<ReturnMessage> responseMessage = channel.send(establishMessage, ReturnMessage.class);

        if (responseMessage.getBody().getResult() == ReturnMessage.RESULT.SUCCEED) {
            System.out.println("连接成功");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String line = scanner.next();
                    Command cmd = new Command("~", line);
                    header.setSession(responseMessage.getHeader().getSession());
                    Message<CommandResult> cmdResult = channel.send(new Message<>(header, cmd), CommandResult.class);
                    logger.info(cmdResult.getBody().getStdOutput());
                } catch (Exception e) {
                    logger.warn("Send Command Exception.", e);
                }
            }
        } else {
            logger.error(responseMessage.getBody().getMessage());
        }
    }
}
