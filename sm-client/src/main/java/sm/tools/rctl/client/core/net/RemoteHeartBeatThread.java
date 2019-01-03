package sm.tools.rctl.client.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.base.utils.string.StringUtil;

import java.io.IOException;
import java.net.InetAddress;

public class RemoteHeartBeatThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RemoteHeartBeatThread.class);
    private static final String configPrefix = "rctl.server.";
    private RctlChannel channel;
    private String host;
    private int port;

    private static final String id = "0000";
    private static final String token = "shumingl";

    public RemoteHeartBeatThread() {
        DynamicHashMap<String, Object> config = ConfigureLoader.prefixConfigMap(configPrefix);
        this.host = config.getString("host");
        this.port = config.getInteger("port");
    }

    @Override
    public void run() {
        try {
            logger.info("server : {}:{}", host, port);
            if (register()) {// 注册
                channel = new RctlChannel(configPrefix);
                long seq = 0;
                while (true) {
                    try {
                        logger.info("Heart Beat SEQ: " + seq);
                        seq = heartBeat(seq);
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        throw e;
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
        try (RctlChannel channel = new RctlChannel(configPrefix)) {

            InetAddress address = NetworkUtils.getLocalHostAddress();
            String macAddress = NetworkUtils.getMacAddress(address);

            HostRegister register = new HostRegister()
                    .withAuth(id, token)
                    .withHost(address.getHostName(), macAddress.replace("-", "").toUpperCase());

            Message<ReturnMessage> returnMessage = channel.send(
                    new Message<>(new Header(id, "register"), register),
                    ReturnMessage.class);

            ReturnMessage retMsg = returnMessage.getBody();
            logger.info("注册[{}]：{}", id, retMsg.getMessage());
            return retMsg.getResult() == ReturnMessage.RESULT.SUCCEED;
        }
    }

    private long heartBeat(long seq) throws Exception {
        // 发送心跳包
        Message<HeartBeat> returnMessage = channel.send(
                new Message<>(new Header(id, "beat"), new HeartBeat(seq)), // 0
                HeartBeat.class);
        Header header = returnMessage.getHeader();
        HeartBeat retBeat = returnMessage.getBody(); // 1
        String action = retBeat.getAction();
        // TODO 此处应该新建会话线程
        if (!StringUtil.isNOE(action)) {
            RemoteSessionThread remoteSessionThread = new RemoteSessionThread(header.getSession());
            Thread thread = new Thread(remoteSessionThread);
            thread.start();
        }
        // 计算下一个序号
        return (retBeat.getSeq() + 1) % RctlConstants.HEART_BEAT_MOD_MAX; // 2
    }

}
