package sm.tools.rctl.remote.module.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.remote.module.client.RctlClient;

import java.net.InetAddress;
import java.net.Socket;

public class HeartBeatThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatThread.class);
    private RctlClient client;
    private String host;
    private int port;

    private static final String id = "0000";
    private static final String token = "shumingl";

    public HeartBeatThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            if (register()) {// 注册
                client = new RctlClient(new Socket(host, port));
                long seq = 0;
                while (true) {
                    try {
                        logger.info("Heart Beat SEQ: " + seq);
                        seq = heartBeat(seq);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        logger.error("Heart Beat Error", e);
                    }
                }
            } else {
                logger.error("主机注册失败。");
            }
        } catch (Exception e) {
            logger.error("Host Register Error", e);
        }
    }

    private boolean register() throws Exception {
        try (RctlClient client = new RctlClient(new Socket(host, port))) {

            InetAddress address = NetworkUtils.getLocalHostAddress();
            String macAddress = NetworkUtils.getMacAddress(address);

            HostRegister register = new HostRegister()
                    .withAuth(id, token)
                    .withHost(address.getHostName(), macAddress.replace("-", "").toUpperCase());

            Message<ReturnMessage> returnMessage = client.send(
                    new Message<>(new Header("register"), register),
                    ReturnMessage.class);

            ReturnMessage retMsg = returnMessage.getBody();
            logger.info("注册[{}]：{}", id, retMsg.getMessage());
            return retMsg.getResult() == ReturnMessage.RESULT.SUCCEED;
        }
    }

    private long heartBeat(long seq) throws Exception {
        // 发送心跳包
        Message<HeartBeat> returnMessage = client.send(
                new Message<>(new Header("beat"), new HeartBeat(seq)), // 0
                HeartBeat.class);
        HeartBeat retBeat = returnMessage.getBody(); // 1
        // 计算下一个序号
        return (retBeat.getSeq() + 1) % RctlConstants.HEART_BEAT_MOD_MAX; // 2
    }

}
