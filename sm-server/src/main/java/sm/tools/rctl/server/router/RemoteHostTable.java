package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.server.router.entity.RemoteHost;

public class RemoteHostTable {
    public static final String CACHE_KEY_HOST = "server.core.hosts";

    public static void put(RemoteHost remoteHost) {
        MemoryCache.put(CACHE_KEY_HOST, remoteHost.getId(), remoteHost);
    }

    public static String getToken(String id) {
        if (exists(id))
            return MemoryCache.<RemoteHost>get(CACHE_KEY_HOST, id).getToken();
        return null;
    }

    public static RemoteHost remove(String session) {
        return MemoryCache.remove(CACHE_KEY_HOST, session);
    }

    public static void merge(RemoteHost remoteHost) {
        if (exists(remoteHost.getId())) {
            RemoteHost host = MemoryCache.get(CACHE_KEY_HOST, remoteHost.getId());
            if (remoteHost.getToken() != null) host.setToken(remoteHost.getToken());
        } else {
            put(remoteHost);
        }
    }

    public static boolean exists(String id) {
        return MemoryCache.contains(CACHE_KEY_HOST, id);
    }
}
