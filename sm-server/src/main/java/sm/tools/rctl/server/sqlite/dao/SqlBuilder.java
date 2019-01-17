package sm.tools.rctl.server.sqlite.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.utils.MapUtil;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.server.utils.ExpressionParser;

import java.util.Map;

public class SqlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);
    private static final ExpressionParser parser = ExpressionParser.getDefault();
    private StringBuilder builder;

    private Object arguments;

    public SqlBuilder(String sql) {
        builder = new StringBuilder(sql);
    }

    public SqlBuilder(String sql, Object arguments) {
        this(sql);
        this.arguments = arguments;
    }

    public SqlBuilder(String sql, Object... arguments) {
        this(sql);
        this.arguments = MapUtil.asMap(arguments);
    }

    public SqlBuilder arguments(Object arguments) {
        this.arguments = arguments;
        return this;
    }

    public SqlBuilder arguments(Object... arguments) {
        this.arguments = MapUtil.asMap(arguments);
        return this;
    }

    public SqlBuilder where(Object object) {
        return option(object, "where", "and");
    }

    public SqlBuilder where(Object object, String join) {
        return option(object, "where", join);
    }

    public SqlBuilder set(Object object) {
        return option(object, "set", ",");
    }

    public SqlBuilder option(Object object, String act, String join) {
        int idx = 0;
        if (object != null) {
            Map<String, Object> arguments = MapUtil.object2map(object);
            for (String field : arguments.keySet()) {
                Object value = arguments.get(field);
                if (value != null) {
                    if (value instanceof String && !StringUtil.isNOE(value)) {
                        if (idx == 0)
                            builder.append(" ").append(act).append(" ");
                        else
                            builder.append(" ").append(join).append(" ");
                        builder.append(field).append(" = '${").append(field).append("}'");
                    }
                    idx++;
                }
            }
        }
        return this;
    }

    public String build() {
        logger.info("SqlBuilder : " + builder.toString());
        return parser.parse(builder.toString(), arguments);
    }

}
