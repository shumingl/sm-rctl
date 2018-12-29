package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class HostRegister {

    @FieldOrder(0)
    private String id;

    @FieldOrder(1)
    private String token;

    @FieldOrder(2)
    private String ip;

    @FieldOrder(3)
    private String mac;

    public HostRegister withAuth(String id, String token) {
        this.id = id;
        this.token = token;
        return this;
    }

    public HostRegister withHost(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
        return this;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
