package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.server.router.entity.SessionRouter;

import java.net.Socket;

public class SessionRouterTable {

    public static final String CACHE_KEY_ROUTER = "server.core.routers";

    public static void put(SessionRouter sessionRouter) {
        MemoryCache.put(CACHE_KEY_ROUTER, sessionRouter.getSession(), sessionRouter);
    }

    public static Socket getClient(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<SessionRouter>get(CACHE_KEY_ROUTER, session).getClient();
        return null;
    }

    public static Socket getRemote(String session) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session))
            return MemoryCache.<SessionRouter>get(CACHE_KEY_ROUTER, session).getRemote();
        return null;
    }

    public static SessionRouter remove(String session) {
        return MemoryCache.remove(CACHE_KEY_ROUTER, session);
    }

    public static void merge(SessionRouter sessionRouter) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, sessionRouter.getSession())) {
            SessionRouter router = MemoryCache.get(CACHE_KEY_ROUTER, sessionRouter.getSession());
            if (sessionRouter.getClient() != null) router.setClient(sessionRouter.getClient());
            if (sessionRouter.getRemote() != null) router.setRemote(sessionRouter.getRemote());
        } else {
            put(sessionRouter);
        }
    }

}
