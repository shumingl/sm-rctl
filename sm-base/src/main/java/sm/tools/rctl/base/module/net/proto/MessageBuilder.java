package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.utils.ProtocolUtils;

import java.nio.charset.Charset;

public class MessageBuilder {
    private StringBuilder builder = new StringBuilder();
    private Charset charset;

    public MessageBuilder(String charset) {
        this.charset = Charset.forName(charset);
    }

    public MessageBuilder append(String... strings) {
        for (String string : strings) {
            if (string == null) string = "";
            int length = string.getBytes(charset).length;
            byte[] bytes = ProtocolUtils.int2bytes(length, 2);
            builder.append(new String(bytes, charset)).append(string);
        }
        return this;
    }

    public byte[] getBytes() {
        return builder.toString().getBytes(this.charset);
    }
}
