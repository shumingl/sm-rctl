package sm.tools.rctl.server.core.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class HostRegister {
    @FieldOrder(0)
    private String ip;
    
    @FieldOrder(1)
    private String mac;

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
