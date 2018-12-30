package sm.tools.rctl.server.router.entity;

import java.net.Socket;
import java.util.UUID;

public class SessionContext {

    private String session;
    private Socket client;
    private Socket remote;

    public SessionContext() {
    }

    public SessionContext(Socket client, Socket remote) {
        this.session = UUID.randomUUID().toString();
        this.client = client;
        this.remote = remote;
    }

    public String getSession() {
        return session;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public Socket getRemote() {
        return remote;
    }

    public void setRemote(Socket remote) {
        this.remote = remote;
    }
}
