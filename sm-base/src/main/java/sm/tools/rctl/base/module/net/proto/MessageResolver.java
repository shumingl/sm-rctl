package sm.tools.rctl.base.module.net.proto;

import org.apache.commons.beanutils.BeanUtils;
import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.utils.ProtocolCache;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.ByteArrayUtils;
import sm.tools.rctl.base.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class MessageResolver<T> {

    private InputStream inputStream;
    private Charset charset;
    private Header header;
    private T body;
    private byte[] bytes;
    private int offset;
    private int length;
    private long timeout;

    public MessageResolver(InputStream inputStream) {
        this(inputStream, RctlConstants.CHARSET_UTF8);
    }

    public MessageResolver(InputStream inputStream, String charset) {
        this(inputStream, Charset.forName(charset));
    }

    public MessageResolver(InputStream inputStream, Charset charset) {
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
    private void resolveLength() throws IOException {
        byte[] bytes = new byte[RctlConstants.TOTAL_LENGTH_BYTES];
        IOUtil.readFixedBytes(inputStream, bytes, timeout);
        length = ProtocolUtils.bytes2int(bytes, 0, bytes.length);
        this.bytes = new byte[length + RctlConstants.TOTAL_LENGTH_BYTES];

        ByteArrayUtils.fill(bytes, this.bytes, 0);
        offset += RctlConstants.TOTAL_LENGTH_BYTES;
    }

    public Message<T> resolve() throws IOException {
        try {
            resolveLength();
            resolveHeader();
            resolveBody();
            return new Message<>(header, body);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("报文解析失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> getActionMessageBodyType() throws ClassNotFoundException {
        Method handler = MemoryCache.get(RctlConstants.CACHE_KEY_HANDLER, header.getAction());
        Type[] types = handler.getGenericParameterTypes();
        String bodyTypeName = ((ParameterizedType) types[1]).getActualTypeArguments()[0].getTypeName();
        return (Class<T>) Class.forName(bodyTypeName);
    }

    private <PART> PART resolvePart(Class<PART> objectClass) throws Exception {
        PART part = objectClass.newInstance();
        List<Field> orderedFields = ProtocolCache.get(objectClass);
        for (Field field : orderedFields) {
            // 解析2字节长度
            byte[] lengthBuffer = read2buffer(RctlConstants.FIELD_LENGTH_BYTES);
            int fieldLength = ProtocolUtils.bytes2int(lengthBuffer);
            // 根据长度解析字段内容
            byte[] fieldBuffer = read2buffer(fieldLength);
            String fieldValue = new String(fieldBuffer, charset);
            // 赋值
            BeanUtils.setProperty(part, field.getName(), fieldValue);
        }
        return part;
    }

    private byte[] read2buffer(int length) throws IOException {
        byte[] bytes = new byte[length];
        IOUtil.readFixedBytes(inputStream, bytes, timeout);
        ByteArrayUtils.fill(bytes, this.bytes, offset);
        offset += length;
        return bytes;
    }

    private void resolveHeader() throws Exception {
        this.header = resolvePart(Header.class);
    }

    private void resolveBody() throws Exception {
        Class<T> bodyClass = getActionMessageBodyType();
        if (bodyClass == null) {
            int remainBytes = length - offset + 1;
            read2buffer(remainBytes);
        } else {
            this.body = resolvePart(bodyClass);
        }
    }

    public MessageResolver<T> withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public T getBody() {
        return body;
    }
}
