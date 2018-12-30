package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class Header {

    @FieldOrder(0)
    private String id;

    @FieldOrder(1)
    private String session;

    @FieldOrder(2)
    private String action;

    public Header() {
    }

    public Header(String id, String session, String action) {
        this.id = id;
        this.session = session;
        this.action = action;
    }

    public Header(String id, String action) {
        this.id = id;
        this.action = action;
    }

    public Header withId(String id) {
        this.id = id;
        return this;
    }

    public Header withSession(String session) {
        this.session = session;
        return this;
    }

    public Header withAction(String action) {
        this.action = action;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
