package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.server.core.RctlChannel;
import sm.tools.rctl.server.router.entity.RctlSession;

public class SessionRouterTable {

    public static final String CACHE_KEY_ROUTER = "server.core.routers";

    public static void put(RctlSession session) {
        MemoryCache.put(CACHE_KEY_ROUTER, session.getSession(), session);
    }

    public static RctlChannel getClient(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<RctlSession>get(CACHE_KEY_ROUTER, session).getClient();
        return null;
    }

    public static RctlChannel getRemote(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<RctlSession>get(CACHE_KEY_ROUTER, session).getRemote();
        return null;
    }

    public static RctlSession remove(String session) {
        return MemoryCache.remove(CACHE_KEY_ROUTER, session);
    }

    public static void merge(RctlSession session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session.getSession())) {
            RctlSession rctlSession = MemoryCache.get(CACHE_KEY_ROUTER, session.getSession());
            if (session.getClient() != null) rctlSession.setClient(session.getClient());
            if (session.getRemote() != null) rctlSession.setRemote(session.getRemote());
        } else {
            put(session);
        }
    }

}
