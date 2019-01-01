package sm.tools.rctl.base.module.net.rctl;

import java.util.UUID;

public class RctlSession {

    private String session;
    private RctlChannel client;
    private RctlChannel remote;

    public RctlSession() {
    }

    public RctlSession(RctlChannel client, RctlChannel remote) {
        this.session = UUID.randomUUID().toString();
        this.client = client;
        this.remote = remote;
    }

    public String getSession() {
        return session;
    }

    public RctlChannel getClient() {
        return client;
    }

    public void setClient(RctlChannel client) {
        this.client = client;
    }

    public RctlChannel getRemote() {
        return remote;
    }

    public void setRemote(RctlChannel remote) {
        this.remote = remote;
    }
}
