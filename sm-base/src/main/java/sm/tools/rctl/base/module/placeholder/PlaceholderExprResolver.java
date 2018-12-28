package sm.tools.rctl.base.module.placeholder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.utils.string.StringUtil;

public class PlaceholderExprResolver {

    private static final Logger logger = LoggerFactory.getLogger(PlaceholderExprResolver.class);

    /**
     * 占位符解析器接口
     */
    public interface PlaceholderResolver {
        String resolvePlaceholder(String placeholderName);
    }

    /**
     * 已知的括号配对
     */
    private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(4);

    static {
        wellKnownSimplePrefixes.put("}", "{");
        wellKnownSimplePrefixes.put("]", "[");
        wellKnownSimplePrefixes.put(")", "(");
    }

    /**
     * 占位符前缀
     */
    private final String placeholderPrefix;
    /**
     * 占位符后缀
     */
    private final String placeholderSuffix;
    /**
     * 后缀匹配的简单前缀，用于处理正常的括号匹配
     */
    private final String simplePrefix;
    /**
     * 默认值分隔符
     */
    private final String valueSeparator;
    /**
     * 是否忽略不能解析的占位符，忽略后，将保留占位符
     */
    private final boolean ignoreUnresolvablePlaceholders;

    public PlaceholderExprResolver(String placeholderPrefix, String placeholderSuffix) {
        this(placeholderPrefix, placeholderSuffix, null, true);
    }

    public PlaceholderExprResolver(String placeholderPrefix, String placeholderSuffix, String valueSeparator,
                                   boolean ignoreUnresolvablePlaceholders) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        } else {
            this.simplePrefix = this.placeholderPrefix.replaceAll("[^\\[\\({]+", "");
        }
        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    /**
     * 占位符表达式解析
     *
     * @param expr                占位符表达式
     * @param placeholderResolver 数值解析器
     * @return
     */
    public String resolve(String expr, PlaceholderResolver placeholderResolver) {
        return parseStringValue(expr, placeholderResolver, new HashSet<>());
    }

    /**
     * 占位符表达式解析
     *
     * @param expr                占位符表达式
     * @param placeholderResolver 占位符解析器
     * @param visitedPlaceholders 标记已经解析过的占位符，用于判断递归引用
     */
    public String parseStringValue(String expr, PlaceholderResolver placeholderResolver,
                                   Set<String> visitedPlaceholders) {

        StringBuilder builder = new StringBuilder(expr);

        int startIndex = expr.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            // 查找匹配的后缀位置
            int endIndex = findPlaceholderEndIndex(builder, startIndex);
            if (endIndex != -1) {
                String placeholder = builder.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                String originalPlaceholder = placeholder;
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // 递归调用，解析占位符中的占位符
                placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
                // 获取解析后的值
                String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                // 如果值为空，取默认值
                if (propVal == null && this.valueSeparator != null) {
                    int separatorIndex = placeholder.indexOf(this.valueSeparator);
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
                        propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    // 递归调用，解析已解析的值中的占位符
                    propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
                    builder.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = builder.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                } else if (this.ignoreUnresolvablePlaceholders) {
                    // 忽略不能解析的占位符
                    startIndex = builder.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                } else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'"
                            + " in string value \"" + expr + "\"");
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }

        return builder.toString();
    }

    /**
     * 查找匹配的后缀位置
     *
     * @param buffer     字符序列
     * @param startIndex 开始位置
     * @return
     */
    private int findPlaceholderEndIndex(CharSequence buffer, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0; // 用于跳过占位符中正常的括号配对
        while (index < buffer.length()) {
            if (StringUtil.substringMatch(buffer, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderSuffix.length();
                } else {
                    return index;
                }
            } else if (StringUtil.substringMatch(buffer, index, this.simplePrefix)) {
                withinNestedPlaceholder++;
                index = index + this.simplePrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        /*
         * Spring占位符解析： 1、可以递归解析 2、可以指定默认值
         */
        PlaceholderExprResolver helper = new PlaceholderExprResolver("${", "}", ":", false);
        String template = "key01=${key01}\n" + "${key02-${key08}:TEST02}\n" + "${${key01}}\n"
                + "${${${${${${${${${key10}}}}}}}}}";

        Map<String, Object> model = new HashMap<>();
        model.put("key01", "value01");
        model.put("value01", "VALUE01");
        model.put("key02-test", "value02-test");
        model.put("key03", "key04");
        model.put("key04", "${key03}");
        model.put("key05", "${key04}");
        model.put("key06", "${key05}");
        model.put("key07", "${key06}");
        model.put("key08", "${key07}");

        model.put("key10", "key11");
        model.put("key11", "key12");
        model.put("key12", "key13");
        model.put("key13", "key14");
        model.put("key14", "key15");
        model.put("key15", "key16");
        model.put("key16", "key17");
        model.put("key17", "key18");
        model.put("key18", "key19");

        String result = helper.resolve(template, new NamedPlaceholderResolver(model));
        System.out.println(result);

    }

}

