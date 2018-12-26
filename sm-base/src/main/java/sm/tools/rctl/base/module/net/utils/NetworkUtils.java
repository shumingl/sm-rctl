package sm.tools.rctl.base.module.net.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkUtils {

    // 获取MAC地址的方法
    public static String getMacAddress(InetAddress ia) throws Exception {
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) builder.append("-");
            String s = Integer.toHexString(mac[i] & 0xFF);// mac[i] & 0xFF 转化为正整数
            builder.append(s.length() == 1 ? 0 + s : s);
        }
        return builder.toString().toUpperCase();
    }


    public static InetAddress getLocalHostAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) { // 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) { // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) { // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

}
