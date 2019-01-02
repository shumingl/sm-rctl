package sm.tools.rctl.server.core;

import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RctlSessionQueue {

    private static final Map<String, List<SessionEstablish>> queue = new ConcurrentHashMap<>();

    private static List<SessionEstablish> getOrCreate(String target) {
        return queue.computeIfAbsent(target, value -> new LinkedList<>());
    }

    public static void add(String target, SessionEstablish sessionEstablish) {
        getOrCreate(target).add(sessionEstablish);
    }

    public static SessionEstablish takeFirst(String target) {
        List<SessionEstablish> list = getOrCreate(target);
        return list.size() > 0 ? list.remove(0) : null;
    }

    public static SessionEstablish takeLast(String target) {
        List<SessionEstablish> list = getOrCreate(target);
        return list.size() > 0 ? list.remove(list.size() - 1) : null;
    }

    public static boolean remove(String target, SessionEstablish sessionEstablish) {
        return getOrCreate(target).remove(sessionEstablish);
    }

    public static boolean isEmpty(String target) {
        return getOrCreate(target).isEmpty();
    }

}
