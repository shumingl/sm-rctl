package sm.tools.rctl.server.sqlite;

import sm.tools.rctl.server.sqlite.service.RctlService;

public class ObjectManager {
    private static final RctlService rctlService = new RctlService();

    public static RctlService getRctlService() {
        return rctlService;
    }
}
