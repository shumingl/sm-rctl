package sm.tools.rctl.server.router;

import sm.tools.rctl.server.router.entity.SessionRouter;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRouterTable {

    private static final Map<String, SessionRouter> router = new ConcurrentHashMap<>();

    public static void put(String session, Socket client, Socket remote) {
        router.put(session, new SessionRouter(session, client, remote));
    }

    public static Socket getClient(String session) {
        if (router.containsKey(session))
            return router.get(session).getClient();
        return null;
    }

    public static Socket getRemote(String session) {
        if (router.containsKey(session))
            return router.get(session).getRemote();
        return null;
    }

    public static SessionRouter remove(String session) {
        return router.remove(session);
    }

    public static void merge(String session, Socket client, Socket remote) {
        if (router.containsKey(session)) {
            SessionRouter sessionRouter = router.get(session);
            if (client != null) sessionRouter.setClient(client);
            if (remote != null) sessionRouter.setRemote(remote);
        } else {
            put(session, client, remote);
        }
    }

}
