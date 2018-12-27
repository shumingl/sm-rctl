package sm.tools.rctl.base.module.net.proto;

import org.apache.commons.beanutils.BeanUtils;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.utils.ProtocolCache;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;

public class MessageResolver<T> {

    private InputStream inputStream;
    private Charset charset;
    private int length;
    private int headerLength;
    private int bodyLength;
    private Header header;
    private T body;

    public MessageResolver(InputStream inputStream, String charset) {
        this(inputStream, Charset.forName(charset));
    }

    public MessageResolver(InputStream inputStream, Charset charset) {
        this.inputStream = inputStream;
        this.charset = charset;
        headerLength = 0;
        bodyLength = 0;
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

    private int resolveLength() throws IOException {
        // 前4个字节是报文总长度，不含长度自身
        byte[] buffer = new byte[RctlConstants.TOTAL_LENGTH_BYTES];
        IOUtil.readFixedBytes(inputStream, buffer);
        length = ProtocolUtils.bytes2int(buffer, 0, buffer.length);
        return length;
    }

    private <PART> PART resolvePart(Class<PART> objectClass) throws Exception {
        PART part = objectClass.newInstance();
        List<Field> orderedFields = ProtocolCache.get(objectClass);
        for (Field field : orderedFields) {
            // 先解析2字节长度
            byte[] fieldLengthBuffer = new byte[RctlConstants.FIELD_LENGTH_BYTES];
            IOUtil.readFixedBytes(inputStream, fieldLengthBuffer);
            int fieldLength = ProtocolUtils.bytes2int(fieldLengthBuffer);
            byte[] fieldBuffer = new byte[fieldLength];
            // 根据长度解析字段内容
            IOUtil.readFixedBytes(inputStream, fieldBuffer);
            String fieldValue = new String(fieldBuffer, charset);
            // 赋值
            BeanUtils.setProperty(part, field.getName(), fieldValue);
            if (Header.class.isAssignableFrom(objectClass))
                headerLength += (fieldLength + fieldLengthBuffer.length);
            else
                bodyLength += (fieldLength + fieldLengthBuffer.length);
        }
        return part;
    }

    public Header resolveHeader() throws Exception {
        this.header = resolvePart(Header.class);
        return header;
    }

    public T resolveBody(Class<T> bodyClass) throws Exception {
        this.body = resolvePart(bodyClass);
        return body;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public Header getHeader() {
        return header;
    }

    public int getLength() {
        return length;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
