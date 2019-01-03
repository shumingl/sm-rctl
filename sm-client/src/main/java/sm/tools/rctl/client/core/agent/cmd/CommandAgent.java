package sm.tools.rctl.client.core.agent.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class CommandAgent {
    private static final Logger logger = LoggerFactory.getLogger(CommandAgent.class);
    public static final Charset CHARSET_GBK = Charset.forName("GBK");

    private String program;
    private Header session;
    private RctlChannel channel;

    public CommandAgent(RctlChannel channel, Header session, String program) throws IOException {
        this.channel = channel;
        this.program = program;
        this.session = session;
    }

    public void exec() throws IOException {

        Process process = Runtime.getRuntime().exec(program);
        new Thread(new CmdResultPrinter(process.getInputStream(), channel, session)).start();
        new Thread(new CmdResultPrinter(process.getErrorStream(), channel, session)).start();

        while (true) {
            try {
                while (!channel.isReadable()) {
                    Thread.sleep(10);
                }
                // 通道可读时，读取Command消息
                Message<Command> cmdMsg = channel.receive(Command.class);
                Header header = cmdMsg.getHeader();
                Command cmdBody = cmdMsg.getBody();
                String base = cmdBody.getBase();
                String command = cmdBody.getCommand();
                logger.info("收到指令{}: {}", header.getSession(), command);

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream(), CHARSET_GBK));

                if (!StringUtil.isNOE(base)) {
                    writer.write("cd \"" + base + "\"");
                    writer.newLine();
                    writer.flush();
                }
                writer.write(command);
                writer.newLine();
                writer.flush();

            } catch (Exception e) {
                logger.error("执行命令异常", e);
            }
        }

    }
}
