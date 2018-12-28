package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class Header {

    @FieldOrder(0)
    private String session;

    @FieldOrder(1)
    private String id;

    @FieldOrder(2)
    private String token;

    @FieldOrder(3)
    private String action;

    public Header() {
    }

    public Header withSession(String session) {
        this.session = session;
        return this;
    }

    public Header withAction(String action) {
        this.action = action;
        return this;
    }

    public Header withAuth(String id, String token) {
        this.id = id;
        this.token = token;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
