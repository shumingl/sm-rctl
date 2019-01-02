package sm.tools.rctl.base.module.net.proto;

import org.apache.commons.beanutils.BeanUtils;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
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

    private InputStream inputStream;
    private Charset charset;
    private Header header;
    private T body;
    private byte[] dataBytes;
    private int offset;
    private int length;
    private long timeout;

    public MessageResolver(InputStream inputStream) throws IOException {
        this(inputStream, RctlConstants.CHARSET_UTF8);
    }

    public MessageResolver(InputStream inputStream, String charset) throws IOException {
        this(inputStream, Charset.forName(charset));
    }

    public MessageResolver(InputStream inputStream, Charset charset) throws IOException {
        this.inputStream = inputStream;
        this.charset = charset;
        this.offset = 0;
        this.timeout = 10000;
    }

    /**
     * 解析总长度字段：前4个字节是报文总长度，不含长度自身
     *
     * @throws IOException IOException
     */
    private void resolveBytes() throws IOException {
        byte[] lengthBytes = new byte[RctlConstants.TOTAL_LENGTH_BYTES];
        IOUtils.readFixedBytes(inputStream, lengthBytes, timeout);
        offset = RctlConstants.TOTAL_LENGTH_BYTES;

        length = ProtocolUtils.bytes2int(lengthBytes);
        dataBytes = new byte[length + offset];

        ByteArrayUtils.fill(lengthBytes, dataBytes, 0);// 总长度
        IOUtils.readFixedBytes(inputStream, dataBytes, offset, length, timeout); // 全部内容
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
        resolveBytes();
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
