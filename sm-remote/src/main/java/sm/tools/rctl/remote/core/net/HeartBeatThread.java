package sm.tools.rctl.remote.core.net;

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
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.remote.core.client.RctlClient;

import java.io.IOException;
import java.net.InetAddress;

public class HeartBeatThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatThread.class);
    private static final String configPrefix = "rctl.server.";
    private RctlChannel channel;
    private String host;
    private int port;

    private static final String id = "0000";
    private static final String token = "shumingl";

    public HeartBeatThread() {
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
        try (RctlClient client = new RctlClient(configPrefix)) {

            InetAddress address = NetworkUtils.getLocalHostAddress();
            String macAddress = NetworkUtils.getMacAddress(address);

            HostRegister register = new HostRegister()
                    .withAuth(id, token)
                    .withHost(address.getHostName(), macAddress.replace("-", "").toUpperCase());

            Message<ReturnMessage> returnMessage = client.send(
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
            SessionEstablish establish = new SessionEstablish(header.getSession());
            channel.send(new Message<>(header, establish), ReturnMessage.class);
        }
        // 计算下一个序号
        return (retBeat.getSeq() + 1) % RctlConstants.HEART_BEAT_MOD_MAX; // 2
    }

}
