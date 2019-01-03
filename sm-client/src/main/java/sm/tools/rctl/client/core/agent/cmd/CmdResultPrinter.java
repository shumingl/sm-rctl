package sm.tools.rctl.client.core.agent.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdResultPrinter extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CmdResultPrinter.class);

    private InputStream inputStream;
    private RctlChannel channel;
    private Header session;

    public CmdResultPrinter(InputStream inputStream, RctlChannel channel, Header session) {
        this.inputStream = inputStream;
        this.channel = channel;
        this.session = session;
    }

    @Override
    public void run() {

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, CommandAgent.CHARSET_GBK);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while (true) {
                while (inputStream.available() <= 0) { // 通道没有数据的时候发送一次数据
                    Thread.sleep(1);
                }
                line = reader.readLine();
                if (line != null) {
                    logger.info("返回：" + line);
                    channel.write(new Message<>(session, CommandResult.SUCCEED(line)));
                } else {
                    logger.info("退出");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(inputStream);
    }
}
