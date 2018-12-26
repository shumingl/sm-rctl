package sm.tools.rctl.base.utils;

import sm.tools.rctl.base.utils.string.StringUtil;

import java.util.*;

@SuppressWarnings("unchecked")
public class MapUtil {

    public static <K, V> Map<K, V> asMap(Object... parameterPairs) {
        Map<K, V> map = new HashMap<>();
        if (parameterPairs != null && parameterPairs.length > 0) {
            for (int i = 0; i < parameterPairs.length; i += 2) {
                Object keyObject = parameterPairs[i];
                Object valueObject = null;
                if (i + 1 < parameterPairs.length)
                    valueObject = parameterPairs[i + 1];
                if (keyObject != null) {
                    map.put((K) keyObject, (V) valueObject);
                }
            }
        }
        return map;
    }

    /**
     * 检查参数
     *
     * @param args 参数
     */
    public static void checkNull(Map<String, ?> args, String... names) {
        if (args == null || args.size() == 0)
            throw new IllegalArgumentException("参数信息为空");
        for (String name : names) {
            if (!args.containsKey(name))
                throw new IllegalArgumentException("缺失指定参数：" + name);
        }
        List<String> values = new ArrayList<>();
        for (String name : names) {
            values.add(String.valueOf(args.get(name)));
        }
        if (StringUtil.isExistsNOE(values.toArray()))
            throw new IllegalArgumentException("部分参数空值：" + Arrays.asList(names));
    }

    /**
     * 以路径的方式获取数据
     *
     * @param data map
     * @param path 路径
     * @return
     */
    public static Object readByPath(Map<?, ?> data, String path) {
        if (data == null) return null;
        if (StringUtil.isNOE(path)) return data;
        int idx = path.indexOf("/");
        // ===================获取前缀路径，判断为List的情况和Map的情况
        String prefixTemp = path.contains("/") ? path.substring(0, idx) : path;
        prefixTemp = prefixTemp.trim();

        int prefixIndex = -1;
        String prefix = "";
        if (prefixTemp.contains("[") && prefixTemp.endsWith("]")) {// List
            int openIndex = prefixTemp.indexOf('[');
            int closeIndex = prefixTemp.indexOf(']');
            if (openIndex > 0) prefix = prefixTemp.substring(0, openIndex);
            // 获取索引号
            String prefixIndexStr = prefixTemp.substring(openIndex + 1, closeIndex);
            if (!StringUtil.isNOE(prefixIndexStr))
                prefixIndex = Integer.parseInt(prefixIndexStr.trim());
        } else { // Map
            prefix = prefixTemp;
        }
        // 如果不含/
        if (!path.contains("/"))
            return data.get(prefix);

        // ===================获取后缀路径
        String suffixString = path.substring(idx + 1);
        Object prefixData = data.get(prefix);// 获取前缀下的Object
        if (prefixData == null) return null;
        if (prefixData instanceof Map) { // 如果是Map
            return readByPath((Map<?, ?>) prefixData, suffixString);
        } else if (prefixData instanceof List) { // 如果是List
            return readByPath((Map<?, ?>) ((List<?>) prefixData).get(prefixIndex), suffixString);
        } else { // 其他
            throw new IllegalArgumentException(String.format("%s is not a Map/List", prefix));
        }
    }
}
