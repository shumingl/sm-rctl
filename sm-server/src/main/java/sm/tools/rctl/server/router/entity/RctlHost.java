package sm.tools.rctl.server.router.entity;

public class RctlHost {

    private String id;
    private String token;

    public RctlHost() {
    }

    public RctlHost(String id, String token) {
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
