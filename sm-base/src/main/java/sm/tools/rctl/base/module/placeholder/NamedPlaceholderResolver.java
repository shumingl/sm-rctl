package sm.tools.rctl.base.module.placeholder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NamedPlaceholderResolver implements PlaceholderExprResolver.PlaceholderResolver {

    /**
     * 数值来源Model
     */
    private Object model;

    /**
     * 不使用系统属性或环境变量
     */
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

    /**
     * 如果没有取到，则使用系统属性或环境变量。默认
     */
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

    /**
     * 优先使用系统属性或环境变量，没有取到则解析Model
     */
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;

    private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

    public NamedPlaceholderResolver(Object model) {
        this.model = model;
    }

    public NamedPlaceholderResolver(Object model, int systemPropertiesMode) {
        this.model = model;
        this.systemPropertiesMode = systemPropertiesMode;
    }

    /**
     * 解析占位符
     */
    @Override
    public String resolvePlaceholder(String placeholder) {
        String propVal = null;
        if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE)
            propVal = resolveSystemProperty(placeholder);
        if (propVal == null)
            propVal = resolveModelValue(placeholder, null);
        if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK)
            propVal = resolveSystemProperty(placeholder);
        return propVal;
    }

    /**
     * 从Model中获取值
     *
     * @param placeholder  占位符
     * @param defaultValue 默认值
     * @return
     */
    public String resolveModelValue(String placeholder, String defaultValue) {
        if (model instanceof Properties) {
            return resolveModelValue((Properties) model, placeholder, defaultValue);
        } else if (model instanceof Map) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) model).entrySet()) {
                map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            return resolveModelValue(map, placeholder, defaultValue);
        } else {
            return resolveModelValue(model, placeholder, defaultValue);
        }
    }

    /**
     * 从Map中获取值
     *
     * @param map          model
     * @param placeholder  占位符
     * @param defaultValue 默认值
     * @return
     */
    private String resolveModelValue(Map<String, String> map, String placeholder, String defaultValue) {
        if (map == null || map.size() == 0)
            return null;
        String value = map.get(placeholder);
        if (value == null)
            return defaultValue;
        return value;
    }

    /**
     * 从Properties中获取值
     *
     * @param properties   model
     * @param placeholder  占位符
     * @param defaultValue 默认值
     * @return
     */
    private String resolveModelValue(Properties properties, String placeholder, String defaultValue) {
        if (properties == null)
            return null;
        return properties.getProperty(placeholder, defaultValue);
    }

    /**
     * 从Java Bean中获取值
     *
     * @param bean         model
     * @param placeholder  占位符
     * @param defaultValue 默认值
     * @return
     */
    private String resolveModelValue(Object bean, String placeholder, String defaultValue) {
        if (bean == null)
            return null;
        String methodName = String.format("get%s%s", placeholder.substring(0, 1).toUpperCase(),
                placeholder.substring(1));
        try {
            Method method = model.getClass().getMethod(methodName);
            String value = String.valueOf(method.invoke(model));
            if (value != null) return value;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    /**
     * 从从系统属性或系统环境变量中获取值
     *
     * @param placeholder 占位符
     * @return
     */
    private String resolveSystemProperty(String placeholder) {
        String value = System.getProperty(placeholder);
        if (value == null)
            value = System.getenv(placeholder);
        return value;
    }

    public void setSystemPropertiesMode(int systemPropertiesMode) throws IllegalArgumentException {
        this.systemPropertiesMode = systemPropertiesMode;
    }

}
