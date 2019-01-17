package sm.tools.rctl.server.sqlite.dao;

import sm.tools.rctl.server.sqlite.ObjectManager;

public class IDGenerator {
    private static final int MAX_LENGTH = 4;

    public static String next() {
        String maxId = ObjectManager.getRctlService().queryMaxId();
        return String.format("%0" + MAX_LENGTH + "d", Integer.parseInt(maxId) + 1);
    }
}
