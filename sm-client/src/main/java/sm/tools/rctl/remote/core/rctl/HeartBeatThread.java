package sm.tools.rctl.remote.core.rctl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.core.ConfigureLoader;
import sm.tools.rctl.base.module.lang.DynamicHashMap;
import sm.tools.rctl.base.module.net.constant.RctlActions;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.RespMsg;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.utils.NetworkUtils;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.remote.core.callback.ProgramThread;

import java.io.IOException;
import java.net.InetAddress;

public class HeartBeatThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatThread.class);
    private static final String serverConfigPrefix = "rctl.server.";
    private static final String remoteConfigPrefix = "rctl.remote.";
    private RctlChannel channel;
    private String host;
    private int port;
    private String id;
    private String token;
    private String nick;

    public HeartBeatThread() {
        DynamicHashMap<String, Object> serverConfig = ConfigureLoader.prefixConfigMap(serverConfigPrefix);
        this.host = serverConfig.getString("host");
        this.port = serverConfig.getInteger("port");
        DynamicHashMap<String, Object> remoteConfig = ConfigureLoader.prefixConfigMap(remoteConfigPrefix);
        this.id = remoteConfig.getString("id");
        this.token = remoteConfig.getString("token");
        this.nick = remoteConfig.getString("nick");
    }

    @Override
    public void run() {
        try {
            logger.info("server : {}:{}", host, port);
            if (register()) {// 注册
                channel = new RctlChannel(serverConfigPrefix);
                long seq = 0;
                while (true) {
                    try {
                        seq = heartBeat(seq);
                        Thread.sleep(500);
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
        try (RctlChannel channel = new RctlChannel(serverConfigPrefix)) {

            InetAddress address = NetworkUtils.getLocalHostAddress();
            String macAddress = NetworkUtils.getMacAddress(address);

            HostRegister register = new HostRegister()
                    .withAuth(id, token, nick)
                    .withHost(address.getHostName(), macAddress.replace("-", "").toUpperCase());

            Message<HostRegister> returnMessage = channel.send(
                    new Message<>(new Header(id, RctlActions.REMOTE_REGISTER), register),
                    HostRegister.class);

            HostRegister host = returnMessage.getBody();
            // TODO 保存注册信息到本地
            logger.info("注册完成：" + host);
            return host != null && host.getId() != null;
        }
    }

    private long heartBeat(long seq) throws Exception {
        // 发送心跳包
        Message<HeartBeat> resp = channel.send(
                new Message<>(new Header(id, RctlActions.REMOTE_HEARTBEAT), new HeartBeat(seq)), // 0
                HeartBeat.class);
        Header header = resp.getHeader();
        HeartBeat retBeat = resp.getBody(); // 1
        String action = retBeat.getAction();
        // 新建会话线程
        if (!StringUtil.isNOE(action)) {
            new Thread(new ProgramThread(header.getSession())).start();
        }
        // 计算下一个序号
        return (retBeat.getSeq() + 1) % RctlConstants.HEART_BEAT_LOOP_MAX; // 2
    }

}
