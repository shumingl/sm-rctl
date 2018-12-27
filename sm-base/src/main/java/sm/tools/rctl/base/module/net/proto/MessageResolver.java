package sm.tools.rctl.base.module.net.proto;

import org.apache.commons.beanutils.BeanUtils;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.utils.ProtocolCache;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.ByteArrayUtils;
import sm.tools.rctl.base.utils.IOUtil;

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
    private byte[] result;
    private int offset;

    public MessageResolver(InputStream inputStream, String charset) {
        this(inputStream, Charset.forName(charset));
    }

    public MessageResolver(InputStream inputStream, Charset charset) {
        this.inputStream = inputStream;
        this.charset = charset;
        this.offset = 0;
    }

    public byte[] getBytes() {
        return result;
    }

    public Message<T> resolve(Class<T> bodyClass) {
        try {
            resolveLength();
            resolveHeader();
            resolveBody(bodyClass);

            return new Message<>(header, body);
        } catch (Exception e) {
            throw new RuntimeException("报文解析失败", e);
        }
    }

    /**
     * 解析总长度字段：前4个字节是报文总长度，不含长度自身
     *
     * @throws IOException IOException
     */
    private void resolveLength() throws IOException {
        byte[] bytes = new byte[RctlConstants.TOTAL_LENGTH_BYTES];
        IOUtil.readFixedBytes(inputStream, bytes);
        int length = ProtocolUtils.bytes2int(bytes, 0, bytes.length);
        result = new byte[length + RctlConstants.TOTAL_LENGTH_BYTES];

        ByteArrayUtils.fill(bytes, result, 0);
        offset += RctlConstants.TOTAL_LENGTH_BYTES;
    }

    private <PART> PART resolvePart(Class<PART> objectClass) throws Exception {
        PART part = objectClass.newInstance();
        List<Field> orderedFields = ProtocolCache.get(objectClass);
        for (Field field : orderedFields) {
            // 解析2字节长度
            byte[] lengthBuffer = read2buffer(inputStream, RctlConstants.FIELD_LENGTH_BYTES);
            int fieldLength = ProtocolUtils.bytes2int(lengthBuffer);
            // 根据长度解析字段内容
            byte[] fieldBuffer = read2buffer(inputStream, fieldLength);
            String fieldValue = new String(fieldBuffer, charset);
            // 赋值
            BeanUtils.setProperty(part, field.getName(), fieldValue);
        }
        return part;
    }

    private byte[] read2buffer(InputStream inputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        IOUtil.readFixedBytes(inputStream, bytes);
        ByteArrayUtils.fill(bytes, result, offset);
        offset += length;
        return bytes;
    }

    private void resolveHeader() throws Exception {
        this.header = resolvePart(Header.class);
    }

    private void resolveBody(Class<T> bodyClass) throws Exception {
        this.body = resolvePart(bodyClass);
    }

    public Header getHeader() {
        return header;
    }

    public T getBody() {
        return body;
    }
}
