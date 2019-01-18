package sm.tools.rctl.base.module.net.rctl;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RctlSession {

    private String session;
    private RctlChannel client;
    private RctlChannel remote;
    private static final AtomicInteger index = new AtomicInteger(0);

    public RctlSession() {
    }

    public RctlSession(String session, RctlChannel client, RctlChannel remote) {
        this.session = session;
        this.client = client;
        this.remote = remote;
    }

    public RctlSession(RctlChannel client, RctlChannel remote) {
        this.session = String.format("%08d", index.getAndIncrement() % 100000000);
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
