package sm.tools.rctl.base.module.net.rctl;

import sm.tools.rctl.base.utils.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class RctlSession implements Closeable {

    private String session;
    private RctlChannel client;
    private RctlChannel remote;
    private static final AtomicInteger index = new AtomicInteger(0);
    private volatile long lastUsed;
    private long timeout = 30000;

    public RctlSession() {
    }

    public RctlSession(String session, RctlChannel client, RctlChannel remote) {
        this.session = session;
        this.client = client;
        this.remote = remote;
        this.lastUsed = System.currentTimeMillis();
    }

    public RctlSession(RctlChannel client, RctlChannel remote) {
        this(generateSessionId(), client, remote);
    }

    public void copyFrom(RctlSession from) {
        this.session = from.session;
        if (from.client != null) this.client = from.client;
        if (from.remote != null) this.remote = from.remote;
        this.lastUsed = System.currentTimeMillis();
    }

    private static String generateSessionId() {
        return String.format("%08d", index.getAndIncrement() % 100000000);
    }

    public void forward() throws IOException {
        this.lastUsed = System.currentTimeMillis();
        System.out.printf("forward/lastUsed=%d\n", lastUsed);
        client.forward(remote, timeout);
    }

    public void receive() throws IOException {
        this.lastUsed = System.currentTimeMillis();
        System.out.printf("receive/lastUsed=%d\n", lastUsed);
        client.receive(remote, timeout);
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

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public boolean isTimeout() {
        System.out.printf("timeout check : %d - %d = %d\n", System.currentTimeMillis(), lastUsed, System.currentTimeMillis() - lastUsed);
        return timeout > 0 && System.currentTimeMillis() - lastUsed > timeout;
    }

    public boolean isClosed() {
        return client != null && client.isClosed() || remote != null && remote.isClosed();
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(client);
        IOUtils.closeQuietly(remote);
    }
}
