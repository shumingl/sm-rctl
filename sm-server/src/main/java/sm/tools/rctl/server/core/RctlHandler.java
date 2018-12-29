package sm.tools.rctl.server.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.Header;
import sm.tools.rctl.base.module.net.proto.Message;
import sm.tools.rctl.base.module.net.proto.MessageBuilder;
import sm.tools.rctl.base.module.net.proto.MessageResolver;
import sm.tools.rctl.server.core.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage.RESULT;
import sm.tools.rctl.server.router.RemoteHostTable;
import sm.tools.rctl.server.router.entity.RemoteHost;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RctlHandler {

    @ActionHandler("register")
    public void remoteRegister(Socket socket, Message<HostRegister> message) throws IOException {

        Logger logger = LoggerFactory.getLogger(RctlHandler.class.getName() + ".remoteRegister");

        Header header = message.getHeader();
        HostRegister body = message.getBody();

        logger.info("开始注册：" + body.getId());
        if (RemoteHostTable.exists(body.getId())) {
            write(socket, new Message<>(header, new ReturnMessage(RESULT.FAILED, "主机已存在")));
            logger.info("主机已存在：" + body.getId());
        } else {
            RemoteHostTable.put(new RemoteHost(body.getId(), body.getToken()));
            write(socket, new Message<>(header, new ReturnMessage(RESULT.SUCCEED, "注册成功")));
            logger.info("注册成功：" + body.getId());
        }
    }

    @ActionHandler("beat")
    public void remoteHeartBeat(Socket socket, Message<HeartBeat> message) {

        Logger logger = LoggerFactory.getLogger(RctlHandler.class.getName() + ".remoteHeartBeat");

        Header header = message.getHeader();
        HeartBeat beat = message.getBody();

        while (true) {
            try {
                if (beat == null) throw new RuntimeException("心跳请求为空");

                // 序号循环递增
                long receive = beat.getSeq();
                long send = (receive + 1) % RctlConstants.HEART_BEAT_MOD_MAX;
                long expect = (send + 1) % RctlConstants.HEART_BEAT_MOD_MAX;

                write(socket, new Message<>(header, new HeartBeat(send)));// 将心跳反馈包发送给客户机，(seq+1) % Long.MAX_VALUE
                Message<HeartBeat> request = read(socket, HeartBeat.class);// 等待客户机发送新的心跳包

                beat = request.getBody();
                if (expect != beat.getSeq())
                    throw new RuntimeException("心跳序号不匹配：expect: " + expect + ", actual: " + beat.getSeq());

            } catch (IOException e) {
                logger.error("客户端连接异常", e);
            } catch (Exception e) {
                logger.error("心跳错误", e);
            }
        }
    }

    private <T> Message<T> read(Socket socket, Class<T> bodyClass) throws IOException {
        return new MessageResolver<T>(socket.getInputStream()).resolve(bodyClass);
    }

    private <T> void write(Socket socket, Message<T> message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(new MessageBuilder<>(message).build());
        outputStream.flush();
    }
}
