package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.annotation.FieldSerializer;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.serialize.Serializer;
import sm.tools.rctl.base.module.net.utils.ProtocolCache;
import sm.tools.rctl.base.module.net.utils.ProtocolUtils;
import sm.tools.rctl.base.utils.ByteArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

public class MessageBuilder<T> {

    private byte[] buffer;
    private byte[] result;
    private int offset;
    private Charset charset;
    private Message<T> message;

    public MessageBuilder(Message<T> message) {
        this(message, RctlConstants.CHARSET_UTF8);
    }

    public MessageBuilder(Message<T> message, Charset charset) {
        this.message = message;
        this.charset = charset;
        this.offset = RctlConstants.TOTAL_LENGTH_BYTES;
        this.buffer = new byte[RctlConstants.MAX_MESSAGE_LENGTH];

        ProtocolCache.put(message.getHeader().getClass());
        ProtocolCache.put(message.getBody().getClass());
    }

    public MessageBuilder<T> withCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public byte[] build() {
        try {
            buildPart(message.getHeader());
            buildPart(message.getBody());
            buildResult();

            return getBytes();
        } catch (Exception e) {
            throw new RuntimeException("构建消息失败", e);
        }
    }

    private void buildResult() {
        int total = offset - RctlConstants.TOTAL_LENGTH_BYTES;
        byte[] byteArray = ProtocolUtils.int2bytes(total, RctlConstants.TOTAL_LENGTH_BYTES);
        ByteArrayUtils.fill(byteArray, buffer, 0);
        result = new byte[offset];
        System.arraycopy(buffer, 0, result, 0, offset);
    }

    private <PART> void buildPart(PART part) throws Exception {
        List<Field> orderedFields = ProtocolUtils.getOrderedFields(part.getClass());
        for (Field field : orderedFields) {
            Method getter = ProtocolCache.getGetter(field);
            Object value = getter.invoke(part);
            FieldSerializer[] serializers = field.getAnnotationsByType(FieldSerializer.class);
            if (serializers != null && serializers.length > 0) {
                Serializer serializer = serializers[0].value().newInstance();
                byte[] fieldBuffer = serializer.serialize(value, charset);
                byte[] lengthBuffer = ProtocolUtils.int2bytes(fieldBuffer.length, RctlConstants.FIELD_LENGTH_BYTES);
                fillField(lengthBuffer, fieldBuffer); // 写入字段长度和字段值
            } else {
                String stringValue = String.valueOf(value);
                byte[] fieldBuffer = stringValue.getBytes(charset);
                byte[] lengthBuffer = ProtocolUtils.int2bytes(fieldBuffer.length, RctlConstants.FIELD_LENGTH_BYTES);
                fillField(lengthBuffer, fieldBuffer); // 写入字段长度和字段值
            }
        }
    }

    private void fillField(byte[]... byteArrays) {
        for (byte[] byteArray : byteArrays) {
            ByteArrayUtils.fill(byteArray, buffer, offset);
            offset += byteArray.length;
        }
    }

    public byte[] getBytes() {
        return result;
    }
}
