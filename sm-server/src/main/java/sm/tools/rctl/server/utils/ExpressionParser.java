package sm.tools.rctl.server.utils;

import org.apache.commons.lang3.text.StrSubstitutor;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解析简单的表达式
 *
 * @author shumingl
 */
@SuppressWarnings("unchecked")
public class ExpressionParser {

    private String prefix;
    private String suffix;
    private static final ConcurrentHashMap<String, ExpressionParser> cache = new ConcurrentHashMap<>();
    private static final ExpressionParser defaultParser = new ExpressionParser("${", "}");

    private ExpressionParser(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * 获取默认的表达式解析类实例
     *
     * @return
     */
    public static ExpressionParser getDefault() {
        return cache.computeIfAbsent("${}", value -> defaultParser);
    }

    /**
     * 根据指定的前缀和后缀获取/创建表达式解析实例
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return
     */
    public static ExpressionParser getInstance(String prefix, String suffix) {
        return cache.computeIfAbsent(prefix + suffix, value -> new ExpressionParser(prefix, suffix));
    }

    /**
     * 判断是否是表达式
     *
     * @param inputString 输入字符串
     * @return
     */
    public boolean isExpr(String inputString) {
        if (StringUtil.isNOE(inputString))
            return false;
        return inputString.contains(prefix) && inputString.contains(suffix);
    }

    /**
     * 表达式替换（默认不递归替换）
     *
     * @param expr  表达式
     * @param model Map
     * @return 替换后的字符串
     */
    public String replace(String expr, Map<String, ?> model) {
        StrSubstitutor substitutor = new StrSubstitutor(model, this.prefix, this.suffix);
        substitutor.setEnableSubstitutionInVariables(false);
        return substitutor.replace(expr);
    }

    /**
     * 表达式替换
     *
     * @param expr        表达式
     * @param model       Map
     * @param isRecursive 是否递归
     * @return 替换后的字符串
     */
    public String replace(String expr, Map<String, ?> model, boolean isRecursive) {
        StrSubstitutor substitutor = new StrSubstitutor(model, this.prefix, this.suffix);
        substitutor.setEnableSubstitutionInVariables(isRecursive);
        return substitutor.replace(expr);
    }

    /**
     * 解析表达式
     *
     * @param expr  expression ${key}
     * @param model Map/Bean
     * @return
     */
    public String parse(String expr, Object model) {

        if (!isExpr(expr)) return expr;
        int left = 0;
        int right = -1;
        String more;
        StringBuilder buffer = new StringBuilder();
        while (expr.indexOf(prefix, left) >= left) {
            left = expr.indexOf(prefix, left); // 从上一次的左标签位置开始查找左标签
            more = left > right + 1 ? expr.substring(right + 1, left) : ""; // 获取非表达式部分的数据
            right = expr.indexOf(suffix, left);// 从当前左标签位置开始查找右标签
            buffer.append(more).append(get(model, expr.substring(left + prefix.length(), right)));
            left = right;
        }
        more = right < expr.length() - 1 ? expr.substring(right + 1) : "";//获取非表达式部分的数据
        buffer.append(more);
        return buffer.toString();
    }

    /**
     * 从Map/Bean中获取key/field对应的值（仅限一层）
     *
     * @param model Map/Bean
     * @param name  key/field
     * @return
     */
    public static Object get(Object model, String name) {
        if (model == null || StringUtil.isNOE(name)) return "";
        try {
            Object value = null;
            if (model instanceof Map) {
                value = ((Map<String, String>) model).get(name);
            } else {
                String methodName = String.format("get%s%s", name.substring(0, 1).toUpperCase(), name.substring(1));
                Method method = model.getClass().getDeclaredMethod(methodName);
                value = String.valueOf(method.invoke(model));
            }
            if (StringUtil.isNOE(value)) // 如果为空则从系统属性中读取变量
                value = System.getProperty(name);
            return (value == null ? "" : value);
        } catch (Exception e) {
            return "";
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
