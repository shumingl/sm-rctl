package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.server.router.entity.SessionRouter;

import java.net.Socket;

public class SessionRouterTable {

    public static final String CACHE_KEY_ROUTER = "server.core.router";

    public static void put(String session, Socket client, Socket remote) {
        MemoryCache.put(CACHE_KEY_ROUTER, session, new SessionRouter(session, client, remote));
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

    public static void merge(String session, Socket client, Socket remote) {
        if (MemoryCache.contains(CACHE_KEY_ROUTER, session)) {
            SessionRouter sessionRouter = MemoryCache.get(CACHE_KEY_ROUTER, session);
            if (client != null) sessionRouter.setClient(client);
            if (remote != null) sessionRouter.setRemote(remote);
        } else {
            put(session, client, remote);
        }
    }

}
