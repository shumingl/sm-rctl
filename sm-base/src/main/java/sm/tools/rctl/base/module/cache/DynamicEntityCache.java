package sm.tools.rctl.base.module.cache;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DynamicEntityCache {

    private static final Map<Class<?>, DynamicEntity> entityMap = new HashMap<>();

    public static DynamicEntity register(Class<?> objectClass) {
        if (!entityMap.containsKey(objectClass)) {
            try {
                DynamicEntity dynamicEntity = new DynamicEntity(objectClass);
                entityMap.put(objectClass, dynamicEntity);
                return dynamicEntity;
            } catch (Exception e) {
                throw new RuntimeException("添加缓存异常", e);
            }
        } else {
            return entityMap.get(objectClass);
        }
    }

    public static <T> T get(Class<?> objectClass, String field) {
        if (!entityMap.containsKey(objectClass))
            register(objectClass);
        DynamicEntity entity = entityMap.get(objectClass);
        if (entity != null)
            return (T) entity.get(field);
        return null;
    }

    public static void set(Class<?> objectClass, String field, Object value) {
        if (!entityMap.containsKey(objectClass))
            register(objectClass);
        DynamicEntity entity = entityMap.get(objectClass);
        if (entity != null)
            entity.set(field, value);
    }

    public static Object getObject(Class<?> objectClass) {
        if (!entityMap.containsKey(objectClass))
            return null;
        return entityMap.get(objectClass).getObject();
    }

}
