package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;
import sm.tools.rctl.base.module.net.annotation.RctlEntity;

@RctlEntity(4)
public class HostConnect {
    @FieldOrder(0)
    private String from;
    @FieldOrder(1)
    private String target;
    @FieldOrder(2)
    private String token;
    @FieldOrder(3)
    private String session;
    @FieldOrder(4)
    private long timeout;

    public HostConnect() {
    }

    public HostConnect(String session) {
        this.session = session;
    }

    public HostConnect(String from, String target, String token) {
        this.from = from;
        this.target = target;
        this.token = token;
    }

    public HostConnect withSession(String session) {
        this.session = session;
        return this;
    }

    public HostConnect withConnect(String from, String target, String token) {
        this.from = from;
        this.target = target;
        this.token = token;
        return this;
    }

    public HostConnect withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
