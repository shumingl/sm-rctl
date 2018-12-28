package sm.tools.rctl.server.core;

import org.junit.Before;
import org.junit.Test;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.core.LogbackConfigure;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.*;
import sm.tools.rctl.server.core.annotation.ActionHandler;
import sm.tools.rctl.server.core.annotation.ActionHandlerScanner;
import sm.tools.rctl.server.core.body.Command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import static org.junit.Assert.*;

public class RctlWorkerTest {
    @Before
    public void setUp() throws Exception {
        ConfigureLoader.loadConfig("config/application.properties");
        LogbackConfigure.configure("config/logback.xml");
        new ActionHandlerScanner("sm.tools.rctl.server.core").scan();
    }

    @ActionHandler("0000")
    public void testCmd(Socket socket, Message<Command> message) {
        MessageBuilder<Command> builder = new MessageBuilder<>(message);
        byte[] result = builder.build();
        MessagePrinter printer = new MessagePrinter(result, RctlConstants.CHARSET_UTF8);
        System.out.println(printer.print());
    }

    @Test
    public void run() throws IOException {
        // 构建请求报文
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        Header header = new Header()
                .withSession(uuid)
                .withAuth("0001", "password")
                .withAction("0000");

        Command commandBody = new Command("%userprofile%", "cmd /c start /b cmd.exe");
        Message<Command> msg = new Message<>(header, commandBody);
        // 序列化，打印
        MessageBuilder<Command> builder = new MessageBuilder<>(msg);
        byte[] result = builder.build();
        MessagePrinter printer = new MessagePrinter(result, RctlConstants.CHARSET_UTF8);
        System.out.println(printer.print());
        // 反序列化
        MessageResolver<?> resolver = new MessageResolver<>(new ByteArrayInputStream(result));
        Message<?> message = resolver.resolve();
        System.out.println(((Command) message.getBody()).getCommand());
    }
}