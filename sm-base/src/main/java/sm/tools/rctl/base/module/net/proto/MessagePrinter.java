package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;

import java.nio.charset.Charset;

public class MessagePrinter {
    private Charset charset;
    private byte[] bytes;
    private int offset;

    public MessagePrinter(byte[] bytes, Charset charset) {
        this.bytes = bytes;
        this.charset = charset;
        this.offset = 0;
    }

    public String print() {

        StringBuilder builder = new StringBuilder();
        int total = ProtocolUtils.bytes2int(bytes, offset, RctlConstants.TOTAL_LENGTH_BYTES);
        offset += RctlConstants.TOTAL_LENGTH_BYTES;

        builder.append("Length: ").append(total).append(", Content: ");
        while (offset < bytes.length) {
            int length = ProtocolUtils.bytes2int(bytes, offset, RctlConstants.FIELD_LENGTH_BYTES);
            offset += RctlConstants.FIELD_LENGTH_BYTES;
            String fieldValue = new String(bytes, offset, length, charset);
            offset += length;
            builder.append("[").append(length).append("]").append(fieldValue);
        }
        return builder.toString();
    }
}
