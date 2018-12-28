package sm.tools.rctl.base.module.net.proto;

import org.junit.Before;
import org.junit.Test;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.constant.RctlConstants;

import java.util.UUID;

public class MessageBuilderTest {
    @Before
    public void setUp() throws Exception {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure("config/logback.xml");
    }

    @Test
    public void build() throws Exception {
        // 构建请求报文
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        Header header = new Header()
                .withSession(uuid)
                .withAuth("0001", "password")
                .withAction("0000");

        CommandBody commandBody = new CommandBody("%userprofile%", "cmd /c start /b cmd.exe");
        Message<CommandBody> msg = new Message<>(header, commandBody);
        // 序列化，打印
        MessageBuilder<CommandBody> builder = new MessageBuilder<>(msg);
        byte[] result = builder.build();
        MessagePrinter printer = new MessagePrinter(result, RctlConstants.CHARSET_UTF8);
        System.out.println(printer.print());
    }
}