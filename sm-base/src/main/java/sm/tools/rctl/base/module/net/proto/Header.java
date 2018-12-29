package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class Header {

    @FieldOrder(0)
    private String session;

    @FieldOrder(1)
    private String action;

    public Header() {
    }

    public Header(String session, String action) {
        this.session = session;
        this.action = action;
    }

    public Header(String action) {
        this.action = action;
    }

    public Header withSession(String session) {
        this.session = session;
        return this;
    }

    public Header withAction(String action) {
        this.action = action;
        return this;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

}
