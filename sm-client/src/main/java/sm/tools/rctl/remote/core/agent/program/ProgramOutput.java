package sm.tools.rctl.remote.core.agent.program;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.CommandResult;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgramOutput extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ProgramOutput.class);
    private static final String remoteConfigPrefix = "rctl.remote.";
    private static final AtomicInteger index = new AtomicInteger(0);

    private InputStream inputStream;
    private String id;
    private String session;
    private RctlChannel channel;

    public ProgramOutput(InputStream inputStream, RctlChannel channel, String session) {
        this.inputStream = inputStream;
        this.session = session;
        this.channel = channel;
        this.id = ConfigureLoader.getString(remoteConfigPrefix + "id");
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CommandResultThread-" + index.getAndIncrement());
        try {
            while (true) {
                while (inputStream.available() <= 0) // 通道没有数据的时候等待
                    Thread.sleep(1);

                int length = inputStream.available();
                byte[] buffer = new byte[length];
                int ret = inputStream.read(buffer);
                String message = new String(buffer, 0, ret, ProgramAgent.DEFAULT_CHARSET);
                Header header = new Header(id, session, "session");
                channel.write(new Message<>(header, CommandResult.SUCCEED(message)));
            }
        } catch (Exception e) {
            logger.error("获取命令结果异常", e);
        }
        IOUtils.closeQuietly(inputStream);
    }
}
