package sm.tools.rctl.server.router.entity;

import java.net.Socket;

public class SessionRouter {

    private String session;
    private Socket client;
    private Socket remote;

    public SessionRouter() {
    }

    public SessionRouter(String session, Socket client, Socket remote) {
        this.session = session;
        this.client = client;
        this.remote = remote;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
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
