package sm.tools.rctl.server.router;

import sm.tools.rctl.base.module.cache.MemoryCache;
import sm.tools.rctl.base.module.net.rctl.RctlHost;

public class RemoteHostTable {
    public static final String CACHE_KEY_HOST = "server.core.hosts";

    public static void put(RctlHost rctlHost) {
        MemoryCache.put(CACHE_KEY_HOST, rctlHost.getId(), rctlHost);
    }

    public static String getToken(String id) {
        if (exists(id))
            return MemoryCache.<RctlHost>get(CACHE_KEY_HOST, id).getToken();
        return null;
    }

    public static RctlHost remove(String session) {
        return MemoryCache.remove(CACHE_KEY_HOST, session);
    }

    public static void merge(RctlHost rctlHost) {
        if (exists(rctlHost.getId())) {
            RctlHost host = MemoryCache.get(CACHE_KEY_HOST, rctlHost.getId());
            if (rctlHost.getToken() != null) host.setToken(rctlHost.getToken());
        } else {
            put(rctlHost);
        }
    }

    public static boolean exists(String id) {
        return MemoryCache.contains(CACHE_KEY_HOST, id);
    }
}
