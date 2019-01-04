package sm.tools.rctl.base.module.net.proto;

import org.apache.commons.beanutils.BeanUtils;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.utils.ProtocolCache;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.ByteArrayUtils;
import sm.tools.rctl.base.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;

public class MessageResolver<T> {

    private RctlChannel channel;
    private Charset charset;
    private Header header;
    private T body;
    private byte[] dataBytes;
    private int offset;
    private int length;
    private long timeout;

    public MessageResolver(RctlChannel channel) throws IOException {
        this(channel, RctlConstants.CHARSET_UTF8);
    }

    public MessageResolver(RctlChannel channel, String charset) throws IOException {
        this(channel, Charset.forName(charset));
    }

    public MessageResolver(RctlChannel channel, Charset charset) throws IOException {
        this(channel.readBytes(), charset);
    }

    public MessageResolver(byte[] dataBytes) {
        this(dataBytes, RctlConstants.CHARSET_UTF8);
    }

    public MessageResolver(byte[] dataBytes, String charset) {
        this(dataBytes, Charset.forName(charset));
    }

    public MessageResolver(byte[] dataBytes, Charset charset) {
        this.dataBytes = dataBytes;
        this.charset = charset;
        this.offset = RctlConstants.TOTAL_LENGTH_BYTES;
        this.timeout = 10000;
        this.length = dataBytes.length - RctlConstants.TOTAL_LENGTH_BYTES;
    }

    public Message<T> resolve(Class<T> bodyClass) throws IOException {
        try {
            resolveHeader();
            resolveBody(bodyClass);
            return new Message<>(header, body);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("报文解析失败", e);
        }
    }

    private <PART> PART resolvePart(Class<PART> objectClass) throws Exception {
        PART part = objectClass.newInstance();
        List<Field> orderedFields = ProtocolCache.get(objectClass);
        for (Field field : orderedFields) {
            int fieldLength = ProtocolUtils.bytes2int(dataBytes, offset, RctlConstants.FIELD_LENGTH_BYTES);
            offset += RctlConstants.FIELD_LENGTH_BYTES;
            String fieldValue = new String(dataBytes, offset, fieldLength, charset);
            offset += fieldLength;
            BeanUtils.setProperty(part, field.getName(), fieldValue);
            // System.out.println("offset=" + offset + ": [" + fieldLength + "]" + ", field=" + fieldValue);
        }
        return part;
    }

    public Header resolveHeader() throws Exception {
        header = resolvePart(Header.class);
        return header;
    }

    public T resolveBody(Class<T> bodyClass) throws Exception {
        if (bodyClass != null)
            body = resolvePart(bodyClass);
        return body;
    }

    public MessageResolver<T> withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public int getLength() {
        return length;
    }
}
