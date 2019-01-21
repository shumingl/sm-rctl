package sm.tools.rctl.server.core.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlSession;
import sm.tools.rctl.base.utils.IOUtils;

public class SessionRouterTable {

    public static final String CACHE_KEY_ROUTER = "server.core.routers";

    public static void put(RctlSession session) {
        MemoryCache.put(CACHE_KEY_ROUTER, session.getSession(), session);
    }

    public static RctlSession getSession(String sessionId) {
        return MemoryCache.get(CACHE_KEY_ROUTER, sessionId);
    }

    public static boolean hasClient(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return getSession(session).getClient() != null;
        return false;
    }

    public static boolean hasRemote(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return getSession(session).getRemote() != null;
        return false;
    }

    public static RctlChannel getClient(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return getSession(session).getClient();
        return null;
    }

    public static RctlChannel getRemote(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return getSession(session).getRemote();
        return null;
    }

    public static RctlSession remove(String session) {
        IOUtils.closeQuietly(getClient(session));
        IOUtils.closeQuietly(getRemote(session));
        return MemoryCache.remove(CACHE_KEY_ROUTER, session);
    }

    public static void merge(RctlSession session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session.getSession())) {
            getSession(session.getSession()).copyFrom(session);
        } else {
            put(session);
        }
    }

}
