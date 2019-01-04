package sm.tools.rctl.remote.core.agent.program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.Command;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class ProgramAgent {
    private static final Logger logger = LoggerFactory.getLogger(ProgramAgent.class);
    public static final Charset DEFAULT_CHARSET = RctlConstants.CHARSET_GBK;

    private String program;
    private String session;
    private RctlChannel channel;

    public ProgramAgent(RctlChannel channel, String session, String program) throws IOException {
        this.channel = channel;
        this.program = program;
        this.session = session;
    }

    public void exec() throws IOException {

        Process process = Runtime.getRuntime().exec(program);
        new Thread(new CmdResultWriter(process.getInputStream(), channel, session)).start();
        new Thread(new CmdResultWriter(process.getErrorStream(), channel, session)).start();
        Header header = null;

        while (true) {
            try {
                while (!channel.isReadable())  // 通道不可读时等待
                    Thread.sleep(1);

                Message<Command> cmd = channel.receive(Command.class);
                header = cmd.getHeader();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream(), DEFAULT_CHARSET));

                writer.write(cmd.getBody().getCommand());
                writer.newLine();
                writer.flush();

            } catch (IOException e) {
                channel.write(new Message<>(header, CommandResult.FAILED(e.toString())));
            } catch (Exception e) {
                logger.error("执行命令异常", e);
            }
        }

    }
}
