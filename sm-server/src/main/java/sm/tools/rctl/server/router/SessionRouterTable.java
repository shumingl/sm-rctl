package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.server.router.entity.SessionContext;

import java.net.Socket;

public class SessionRouterTable {

    public static final String CACHE_KEY_ROUTER = "server.core.routers";

    public static void put(SessionContext sessionContext) {
        MemoryCache.put(CACHE_KEY_ROUTER, sessionContext.getSession(), sessionContext);
    }

    public static Socket getClient(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<SessionContext>get(CACHE_KEY_ROUTER, session).getClient();
        return null;
    }

    public static Socket getRemote(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<SessionContext>get(CACHE_KEY_ROUTER, session).getRemote();
        return null;
    }

    public static SessionContext remove(String session) {
        return MemoryCache.remove(CACHE_KEY_ROUTER, session);
    }

    public static void merge(SessionContext sessionContext) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, sessionContext.getSession())) {
            SessionContext context = MemoryCache.get(CACHE_KEY_ROUTER, sessionContext.getSession());
            if (sessionContext.getClient() != null) context.setClient(sessionContext.getClient());
            if (sessionContext.getRemote() != null) context.setRemote(sessionContext.getRemote());
        } else {
            put(sessionContext);
        }
    }

}
