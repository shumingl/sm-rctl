package sm.tools.rctl.base.module.net.proto;

import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.base.module.net.annotation.FieldOrder;

import java.net.InetAddress;

public class Header {

    @FieldOrder(0)
    private String msgId;

    @FieldOrder(1)
    private String id;

    @FieldOrder(2)
    private String token;

    @FieldOrder(3)
    private String nick;

    @FieldOrder(4)
    private String ip;

    @FieldOrder(5)
    private String mac;

    @FieldOrder(6)
    private String username;

    @FieldOrder(7)
    private String password;

    @FieldOrder(8)
    private int index;

    @FieldOrder(9)
    private int total;

    public Header(String msgId, InetAddress address) {
        try {
            this.ip = address.getHostAddress();
            this.mac = NetworkUtils.getMacAddress(address);
            this.index = 0;
            this.total = 1;
            this.msgId = msgId;
        } catch (Exception e) {
            throw new RuntimeException("获取本机网卡信息失败", e);
        }
    }

    public Header withId(String id) {
        this.id = id;
        return this;
    }

    public Header withNick(String nick) {
        this.nick = nick;
        return this;
    }

    public Header withToken(String token) {
        this.token = token;
        return this;
    }

    public Header withAuth(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public Header withStat(int index, int total) {
        this.index = index;
        this.total = total;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
