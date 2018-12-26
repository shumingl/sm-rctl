package sm.tools.rctl.base.utils;

import sm.tools.rctl.base.utils.string.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReflectUtil {

    /**
     * 生成方法名
     *
     * @param action get/set/is/add/put etc.
     * @param name   字段名
     */
    public static String genMethod(String action, String name) {
        if (StringUtil.isNOE(name)) return action;
        return action + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 根据方法名获取字段名
     *
     * @param method 方法
     * @param action get/set/is/add/put
     */
    public static String parseField(Method method, String action) {
        return parseField(method.getName(), action);
    }

    /**
     * 根据方法名获取字段名
     *
     * @param method 方法
     * @param action get/set/is/add/put
     */
    public static String parseField(String method, String action) {
        if (StringUtil.isNOE(action))
            throw new IllegalArgumentException("action is null, such as get/set/is/add/put etc.");
        int len = action.length();
        if (method.startsWith(action))
            return method.substring(len, len + 1).toLowerCase() + method.substring(len + 1);
        return null;
    }

    /**
     * 根据方法组获取一组字段名
     *
     * @param methods 方法
     * @param action  get/set/is/add/put
     */
    public static String[] parseFields(String[] methods, String action) {
        if (methods == null) return new String[0];
        final List<String> fields = new ArrayList<>();
        Arrays.asList(methods).forEach(method -> fields.add(parseField(method, action)));
        return (String[]) fields.toArray();
    }

    /**
     * 根据方法组获取一组字段名
     *
     * @param methods 方法
     * @param action  get/set/is/add/put
     */
    public static String[] parseFields(Method[] methods, String action) {
        if (methods == null) return new String[0];
        final List<String> fields = new ArrayList<>();
        Arrays.asList(methods).forEach(method -> fields.add(parseField(method, action)));
        return (String[]) fields.toArray();
    }

    /**
     * 根据方法名获取一组方法
     *
     * @param clazz      类
     * @param methodName 方法名
     */
    public static List<Method> getMethodsByName(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (methodName.equals(method.getName()))
                result.add(method);
        }
        return result;
    }

    /**
     * 根据方法名获取方法
     *
     * @param clazz      类
     * @param methodName 方法名
     */
    public static Method getMethodByName(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName()))
                return method;
        }
        return null;
    }

    /**
     * 获取全部字段
     *
     * @param clazz 类
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) return null;
        List<Field> list = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        List<Field> superList = getAllFields(clazz.getSuperclass());
        if (superList != null) list.addAll(superList);
        return list;
    }

    /**
     * 获取全部方法
     *
     * @param clazz 类
     */
    public static List<Method> getAllMethods(Class<?> clazz) {
        if (clazz == null) return null;
        List<Method> list = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        List<Method> superList = getAllMethods(clazz.getSuperclass());
        if (superList != null) list.addAll(superList);
        return list;
    }

    /**
     * 判断一个类是否是其他类的子类或实现了其他接口
     *
     * @param child   要判断的类
     * @param parents 父类或接口 SuperClass Or Interface
     */
    public static boolean isSubClass(Class<?> child, Class<?>... parents) {
        if (child == null || parents == null) return false;
        for (Class<?> parent : parents)
            if (parent.isAssignableFrom(child))
                return true;
        return false;
    }

    /**
     * 获取类的某个数组字段的元素类型
     *
     * @param objectClass 类
     * @param field       字段名
     * @return 元素类型
     */
    public static Class<?> getArrayElementType(Class<?> objectClass, String field) throws NoSuchFieldException {
        Class<?> fieldType = objectClass.getDeclaredField(field).getType();
        if (fieldType.isArray())
            return fieldType.getComponentType();
        return null;
    }

    /**
     * 获取类的List字段的元素类型
     *
     * @param objectClass 类
     * @param field       字段名
     * @return 元素类型
     */
    public static Class<?> getListElementType(Class<?> objectClass, String field) throws NoSuchFieldException {
        Class<?> fieldType = objectClass.getDeclaredField(field).getType();
        Type genericType = objectClass.getDeclaredField(field).getGenericType();
        if (ParameterizedType.class.isAssignableFrom(genericType.getClass()) &&
                Collection.class.isAssignableFrom(fieldType)) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return null;
    }

}
