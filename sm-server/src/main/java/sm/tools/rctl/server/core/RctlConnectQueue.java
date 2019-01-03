package sm.tools.rctl.server.core;

import sm.tools.rctl.base.module.net.proto.body.HostConnect;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RctlConnectQueue {

    private static final Map<String, List<HostConnect>> queue = new ConcurrentHashMap<>();

    private static List<HostConnect> getOrCreate(String target) {
        return queue.computeIfAbsent(target, value -> new LinkedList<>());
    }

    public static void add(String target, HostConnect hostConnect) {
        getOrCreate(target).add(hostConnect);
    }

    public static HostConnect takeFirst(String target) {
        List<HostConnect> list = getOrCreate(target);
        return list.size() > 0 ? list.remove(0) : null;
    }

    public static HostConnect takeLast(String target) {
        List<HostConnect> list = getOrCreate(target);
        return list.size() > 0 ? list.remove(list.size() - 1) : null;
    }

    public static boolean remove(String target, HostConnect hostConnect) {
        return getOrCreate(target).remove(hostConnect);
    }

    public static boolean isEmpty(String target) {
        return getOrCreate(target).isEmpty();
    }

}
