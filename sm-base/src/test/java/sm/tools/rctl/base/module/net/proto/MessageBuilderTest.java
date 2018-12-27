package sm.tools.rctl.base.module.net.proto;

import org.junit.Test;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.body.CommandBody;

import java.io.ByteArrayInputStream;
import java.util.UUID;

public class MessageBuilderTest {

    @Test
    public void build() {
        // 构建请求报文
        String uuid = UUID.randomUUID().toString();
        Header header = new Header(uuid);
        header.withId("123456").withNick("shumingl").withToken("ABCDEFGHIJKLMN")
                .withAuth("shumingl", "password")
                .withStat(0, 1);

        CommandBody commandBody = new CommandBody("%userprofile%", "cmd /c start /b cmd.exe");
        Message<CommandBody> msg = new Message<>(header, commandBody);
        // 序列化，打印
        MessageBuilder<CommandBody> builder = new MessageBuilder<>(RctlConstants.CHARSET_UTF8);
        byte[] result = builder.withMessage(msg).build();
        MessagePrinter printer = new MessagePrinter(result, RctlConstants.CHARSET_UTF8);
        System.out.println("Data    : " + printer.print());
        // 反序列化
        ByteArrayInputStream inputStream = new ByteArrayInputStream(result);
        MessageResolver<CommandBody> resolver = new MessageResolver<>(inputStream, RctlConstants.CHARSET_UTF8);
        Message<CommandBody> message = resolver.resolve(CommandBody.class);
        System.out.println("Command : " + message.getBody().getCommand());
    }
}