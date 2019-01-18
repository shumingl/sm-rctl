package sm.tools.rctl.client.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.constant.RctlActions;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.proto.body.RespMsg;
import sm.tools.rctl.base.module.net.proto.body.HostConnect;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;

import java.io.IOException;
import java.util.Scanner;

public class ClientStartup {

    private static final Logger logger = LoggerFactory.getLogger(ClientStartup.class);
    private static volatile boolean terminated = false;

    public static void main(String[] args) throws IOException {
        startup();
    }

    public static void startup() throws IOException {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure(ConfigureLoader.getString("logback.config"));

        RctlChannel channel = new RctlChannel("rctl.server.");

        Header header = new Header("0000", RctlActions.CLIENT_CONNECT);
        HostConnect establish = new HostConnect("0000", "0000", "123456");
        Message<HostConnect> establishMessage = new Message<>(header, establish);
        Message<RespMsg> resp = channel.send(establishMessage, RespMsg.class);

        if (resp.getBody().getResult() == RespMsg.RESULT.SUCCEED) {

            new Thread(() -> {
                Thread.currentThread().setName("CONSOLE-" + resp.getHeader().getSession());
                try {
                    while (!terminated) {
                        Message<CommandResult> cmdResult = channel.receive(CommandResult.class);
                        System.out.print(cmdResult.getBody().getStdOutput());
                        if (cmdResult.getBody().isTerminated()) {
                            terminated = true;
                            System.out.println();
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("读取命令结果异常", e);
                }
                System.out.println("通道已经关闭。");
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    if (terminated) break;
                    String line = scanner.nextLine();
                    channel.write(new Message<>(
                            header.withSession(resp.getHeader().getSession()),
                            new Command(line)));
                } catch (Exception e) {
                    logger.warn("Send Command Exception.", e);
                }
            }
            System.out.println("程序执行结束。");
        } else {
            logger.error(resp.getBody().getMessage());
        }
    }
}
