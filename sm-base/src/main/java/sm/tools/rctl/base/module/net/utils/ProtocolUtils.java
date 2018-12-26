package sm.tools.rctl.base.module.net.utils;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolUtils {

    /**
     * 整数转换为大端（高字节在前）表示的字节数组
     *
     * @param value  整数
     * @param length 长度
     * @return byte[length]
     */
    public static byte[] int2bytes(int value, int length) {

        if (length < 0) return new byte[0];

        byte[] bytes = new byte[length];
        int limit = Math.min(length, 4);

        for (int i = 0; i < limit; i++)
            bytes[limit - i - 1] = (byte) (value >> (i * 8) & 0xff);

        return bytes;

    }

    public static int bytes2int(byte[] bytes) {
        return bytes2int(bytes, 0, bytes.length);
    }

    public static int bytes2int(byte[] bytes, int offset, int length) {

        if (bytes == null) return -1;

        int limit = Math.min(length, 4);
        int value = 0;
        for (int i = 0; i < limit; i++) {
            value |= ((bytes[offset + i] & 0xFF) << ((limit - i - 1) * 8));
        }
        return value;
    }

    public static List<Field> getOrderedFields(Class<?> objectClass) {
        List<Field> fields = ReflectUtil.getAllFields(objectClass);
        Map<String, Integer> orderMap = new HashMap<>();
        for (Field field : fields) {
            FieldOrder[] fieldOrders = field.getAnnotationsByType(FieldOrder.class);
            if (fieldOrders == null || fieldOrders.length == 0) {
                throw new RuntimeException("字段需要指定FieldOrder排序：Class: " +
                        objectClass.getTypeName() + ", Field: " + field.getName());
            } else {
                orderMap.put(field.getName(), fieldOrders[0].value());
            }
        }
        fields.sort(Comparator.comparingInt(f -> orderMap.get(f.getName())));
        return fields;
    }

    public static void main(String[] args) {
        int value = Integer.MAX_VALUE;
        byte[] bytes = int2bytes(value, 4);
        System.out.println(bytes2int(bytes));
        System.out.println(getOrderedFields(Header.class));
    }
}
