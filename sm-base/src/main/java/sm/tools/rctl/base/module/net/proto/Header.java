package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.base.module.net.annotation.FieldOrder;

import java.net.InetAddress;

public class Header {

    @FieldOrder(0)
    private String transCode;

    @FieldOrder(1)
    private String session;

    @FieldOrder(2)
    private String id;

    @FieldOrder(3)
    private String password;

    @FieldOrder(4)
    private String nick;

    @FieldOrder(5)
    private String ip;

    @FieldOrder(6)
    private String mac;

    @FieldOrder(7)
    private int index;

    @FieldOrder(8)
    private int total;

    @FieldOrder(9)
    private String target;

    @FieldOrder(10)
    private String bodyType;

    public Header() {
    }

    public Header(String session) {
        try {
            InetAddress address = NetworkUtils.getLocalHostAddress();
            String macAddress = NetworkUtils.getMacAddress(address);

            this.ip = address.getHostAddress();
            this.mac = macAddress.replace("-", "");
            this.index = 0;
            this.total = 1;
            this.session = session;

        } catch (Exception e) {
            throw new RuntimeException("获取本机网卡信息失败", e);
        }
    }

    public Header withTransCode(String transCode) {
        this.transCode = transCode;
        return this;
    }

    public Header withNick(String nick) {
        this.nick = nick;
        return this;
    }

    public Header withBodyType(String bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public Header withAuth(String id, String target, String password) {
        this.id = id;
        this.target = target;
        this.password = password;
        return this;
    }

    public Header withStat(int index, int total) {
        this.index = index;
        this.total = total;
        return this;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
}
