package sm.tools.rctl.base.module.net.constant;

import java.nio.charset.Charset;

public class RctlConstants {

    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    public static final Charset CHARSET_GBK = Charset.forName("GBK");
    public static final Charset CHARSET_GB2312 = Charset.forName("GB2312");

    public static final int TOTAL_LENGTH_BYTES = 4;
    public static final int FIELD_LENGTH_BYTES = 2;

    public static final int MAX_MESSAGE_LENGTH = 1024 * 1024;

    public static final String CACHE_KEY_HANDLER = "server.core.handlers";

}
