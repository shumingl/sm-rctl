package sm.tools.rctl.base.utils;

import java.math.BigDecimal;

public class ObjectUtil {

    public static boolean isBoolean(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean)
            return true;
        try {
            String objectString = String.valueOf(object);
            return "true".equalsIgnoreCase(objectString) || "false".equalsIgnoreCase(objectString);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNumeric(Object object) {
        if (object == null) return false;
        if (object instanceof Number)
            return true;
        try {
            new BigDecimal(String.valueOf(object));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Byte getByte(Object object) {
        return getInteger(object).byteValue();
    }

    public static Character getChar(Object object) {
        if (object == null) return null;
        String str = String.valueOf(object);
        if (!"".equals(str))
            return String.valueOf(object).charAt(0);
        return null;
    }

    public static String getString(Object object) {
        if (object == null) return null;
        return String.valueOf(object);
    }

    public static Integer getInteger(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).intValue();
        return Integer.parseInt(String.valueOf(object));
    }

    public static Short getShort(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).shortValue();
        return Short.parseShort(String.valueOf(object));
    }

    public static Long getLong(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).longValue();
        return Long.parseLong(String.valueOf(object));
    }

    public static Float getFloat(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).floatValue();
        return Float.parseFloat(String.valueOf(object));
    }

    public static Double getDouble(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return ((Number) object).doubleValue();
        return Double.parseDouble(String.valueOf(object));
    }

    public static BigDecimal getDecimal(Object object) {
        if (object == null) return null;
        return new BigDecimal(String.valueOf(object));
    }

    public static Number getNumber(Object object) {
        if (object == null) return null;
        if (object instanceof Number)
            return (Number) object;
        return Double.parseDouble(String.valueOf(object));
    }

    public static Boolean getBoolean(Object object) {
        if (object == null) return null;
        if (object instanceof Boolean)
            return (Boolean) object;
        return Boolean.parseBoolean(String.valueOf(object));
    }

}
