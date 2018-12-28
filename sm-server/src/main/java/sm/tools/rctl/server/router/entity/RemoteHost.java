package sm.tools.rctl.server.router.entity;

public class RemoteHost {

    private String id;
    private String token;

    public RemoteHost() {
    }

    public RemoteHost(String id, String token) {
        this.id = id;
        this.token = token;
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
