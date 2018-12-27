package sm.tools.rctl.base.module.net.utils;

import sm.tools.rctl.base.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolCache {

    private static final Map<Class<?>, List<Field>> classOrderedFieldsCache = new ConcurrentHashMap<>();
    private static final Map<Field, Method> classGetter = new ConcurrentHashMap<>();
    private static final Map<Field, Method> classSetter = new ConcurrentHashMap<>();

    public static void put(Class<?> objectClass) {
        classOrderedFieldsCache.putIfAbsent(objectClass, resolve(objectClass));
    }

    private static List<Field> resolve(Class<?> objectClass) {
        List<Field> fields = ProtocolUtils.getOrderedFields(objectClass);
        if (fields != null) {
            for (Field field : fields) {
                String getAction = Boolean.class.isAssignableFrom(field.getType()) ? "is" : "get";
                String getName = ReflectUtil.genMethod(getAction, field.getName());
                String setName = ReflectUtil.genMethod("set", field.getName());
                classGetter.put(field, ReflectUtil.getMethodByName(objectClass, getName));
                classSetter.put(field, ReflectUtil.getMethodByName(objectClass, setName));
            }
        }
        return fields;
    }

    public static Method getGetter(Field field) {
        return classGetter.get(field);
    }

    public static Method getSetter(Field field) {
        return classSetter.get(field);
    }

    public static List<Field> get(Class<?> objectClass) {
        return classOrderedFieldsCache.putIfAbsent(objectClass, resolve(objectClass));
    }

    public static List<Field> remove(Class<?> objectClass) {
        return classOrderedFieldsCache.remove(objectClass);
    }

    public static boolean exists(Class<?> objectClass) {
        return classOrderedFieldsCache.containsKey(objectClass);
    }

}
