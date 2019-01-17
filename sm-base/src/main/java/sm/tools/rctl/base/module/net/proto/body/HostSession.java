package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;
import sm.tools.rctl.base.module.net.annotation.RctlEntity;

@RctlEntity(6)
public class HostSession {
    @FieldOrder(0)
    private String session;

    public HostSession() {
    }

    public HostSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

}
