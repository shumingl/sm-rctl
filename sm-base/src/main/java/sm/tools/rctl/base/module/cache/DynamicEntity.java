package sm.tools.rctl.base.module.cache;

import org.springframework.util.Assert;
import sm.tools.rctl.base.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicEntity {

    private Object object;
    private Map<String, Method> gets = new HashMap<>();
    private Map<String, Method> sets = new HashMap<>();

    public Object get(String name) {
        Assert.notNull(name, "字段名不能为空");
        name = name.toLowerCase();
        if (!gets.containsKey(name))
            return null;
        try {
            return gets.get(name).invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("执行get方法异常：" + name, e);
        }
    }

    public void set(String name, Object value) {
        Assert.notNull(name, "字段名不能为空");
        name = name.toLowerCase();
        if (!sets.containsKey(name))
            return;
        try {
            sets.get(name).invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("执行set方法异常：" + name, e);
        }
    }

    private void init() {

        Class<?> objectClass = object.getClass();

        List<Field> fields = ReflectUtil.getAllFields(objectClass);
        List<Method> methods = ReflectUtil.getAllMethods(objectClass);
        for (Field field : fields) {
            String name = field.getName();
            String lowerName = name.toLowerCase();
            String gn = ReflectUtil.genMethod("get", name);
            String sn = ReflectUtil.genMethod("set", name);
            for (Method m : methods) {
                String mn = m.getName();
                if (mn.equals(gn) && m.getParameterCount() == 0)
                    gets.put(lowerName, m);
                if (mn.equals(sn) && m.getParameterCount() == 1)
                    sets.put(lowerName, m);
            }
        }
    }

    public DynamicEntity(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this(Class.forName(className));
    }

    public DynamicEntity(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        this(clazz.newInstance());
    }

    public DynamicEntity(Object object) {
        Assert.notNull(object, "参数不能为空");
        this.object = object;
        init();
    }

    public <T> T getObject() {
        return (T) object;
    }
}
