package sm.tools.rctl.server.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostConnect;
import sm.tools.rctl.base.module.net.rctl.RctlChannel;
import sm.tools.rctl.base.module.net.rctl.RctlHandler;
import sm.tools.rctl.server.core.RctlConnectQueue;

import java.io.IOException;

@ActionHandler("beat")
public class HeartBeatHandler implements RctlHandler<HeartBeat> {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void handle(RctlChannel channel, Message<HeartBeat> message) throws IOException {

        Header header = message.getHeader();
        HeartBeat beat = message.getBody();

        while (true) {
            try {
                if (beat == null) throw new RuntimeException("心跳请求为空");

                // 序号循环递增
                long receive = beat.getSeq();
                long send = (receive + 1) % RctlConstants.HEART_BEAT_MOD_MAX;
                long expect = (send + 1) % RctlConstants.HEART_BEAT_MOD_MAX;
                HeartBeat heartBeat = new HeartBeat(send);

                // 查询是否有客户机连接请求
                HostConnect establish = RctlConnectQueue.takeFirst(header.getId());
                if (establish != null) {
                    heartBeat.setAction("session");
                    header.setSession(establish.getSession());
                }

                // 发送反馈包给客户机，并接收客户机心的心跳包
                Message<HeartBeat> request = channel.send(new Message<>(header, heartBeat), HeartBeat.class);

                beat = request.getBody();
                if (expect != beat.getSeq())
                    throw new RuntimeException("心跳序号不匹配：expect: " + expect + ", actual: " + beat.getSeq());

            } catch (IOException e) {
                logger.error("客户端连接异常", e);
                throw e;
            } catch (Exception e) {
                logger.error("心跳错误", e);
            }
        }
    }
}
