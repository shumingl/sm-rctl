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
import sm.tools.rctl.base.utils.string.StringUtil;

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

        RctlChannel channel = new RctlChannel("rctl.server.");

        Header header = new Header("0000", "control");
        HostConnect establish = new HostConnect("0000", "0000", "shumingl");
        Message<HostConnect> establishMessage = new Message<>(header, establish);
        Message<ReturnMessage> resp = channel.send(establishMessage, ReturnMessage.class);

        if (resp.getBody().getResult() == ReturnMessage.RESULT.SUCCEED) {

            new Thread(() -> {
                Thread.currentThread().setName("CommandResultReader");
                try {
                    while (true) {
                        Message<CommandResult> cmdResult = channel.receive(CommandResult.class);
                        System.out.print(cmdResult.getBody().getStdOutput());
                    }
                } catch (Exception e) {
                    logger.error("读取命令结果异常", e);
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String line = scanner.nextLine();
                    channel.write(new Message<>(
                            header.withSession(resp.getHeader().getSession()),
                            new Command(line)));

                } catch (Exception e) {
                    logger.warn("Send Command Exception.", e);
                }
            }
        } else {
            logger.error(resp.getBody().getMessage());
        }
    }
}
