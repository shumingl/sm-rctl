package sm.tools.rctl.server.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sm.tools.rctl.base.module.net.constant.RctlConstants;
import sm.tools.rctl.base.module.net.proto.*;
import sm.tools.rctl.base.module.net.proto.body.SessionEstablish;
import sm.tools.rctl.base.utils.string.StringUtil;
import sm.tools.rctl.base.module.net.annotation.ActionHandler;
import sm.tools.rctl.base.module.net.proto.body.HeartBeat;
import sm.tools.rctl.base.module.net.proto.body.HostRegister;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage;
import sm.tools.rctl.base.module.net.proto.body.ReturnMessage.RESULT;
import sm.tools.rctl.server.router.RemoteHostTable;
import sm.tools.rctl.server.router.SessionRouterTable;
import sm.tools.rctl.server.router.entity.RemoteHost;
import sm.tools.rctl.server.router.entity.SessionContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class RctlServerHandler {

    @ActionHandler("register")
    public void remoteRegister(Socket socket, Message<HostRegister> message) throws IOException {

        Logger logger = LoggerFactory.getLogger(RctlServerHandler.class.getName() + ".remoteRegister");

        Header header = message.getHeader();
        HostRegister body = message.getBody();

        logger.info("开始注册：" + body.getId());
        if (RemoteHostTable.exists(body.getId())) {
            response(socket, new Message<>(header, new ReturnMessage(RESULT.FAILED, "主机已存在")));
            logger.info("主机已存在：" + body.getId());
        } else {
            RemoteHostTable.put(new RemoteHost(body.getId(), body.getToken()));
            response(socket, new Message<>(header, new ReturnMessage(RESULT.SUCCEED, "注册成功")));
            logger.info("注册成功：" + body.getId());
        }
    }

    @ActionHandler("establish")
    public void clientSessionEstablish(Socket socket, Message<SessionEstablish> message) throws IOException {
        Logger logger = LoggerFactory.getLogger(RctlServerHandler.class.getName() + ".clientSessionEstablish");
        Header header = message.getHeader();
        SessionEstablish establish = message.getBody();

        long defaultTimeout = 10000L;

        try {
            if (!StringUtil.isNOE(establish.getSession())) { // 远程机响应建立会话

                logger.info("远程响应：{}->{}", establish.getSession());
                // 登记远程机Socket
                SessionContext context = new SessionContext(null, socket);
                SessionRouterTable.merge(context); // 更新会话信息
                ReturnMessage retMsg = new ReturnMessage(RESULT.SUCCEED, "连接成功");
                response(socket, new Message<>(header, retMsg));

            } else { // 客户机请求建立会话

                logger.info("请求会话：{}->{}", establish.getFrom(), establish.getTarget());
                SessionQueue.add(establish.getTarget(), establish);
                logger.info("登记完成：{}->{}", establish.getFrom(), establish.getTarget());

                SessionContext context = new SessionContext(socket, null);
                SessionRouterTable.put(context);

                // 等待远程机Socket
                Socket remote = null;
                String sessionId = context.getSession();

                long start = System.currentTimeMillis();
                long timeout = establish.getTimeout();
                if (timeout <= 0) timeout = defaultTimeout;

                // 远程机没有发起连接并且没有超时，就等待远程机
                while (remote == null && System.currentTimeMillis() - start < timeout) {
                    remote = SessionRouterTable.getRemote(sessionId);
                    Thread.sleep(1);
                }
                establish.setSession(sessionId);
                response(socket, new Message<>(header, establish));

            }
        } catch (IOException e) {

        } catch (Exception e) {

        }

    }

    @ActionHandler("beat")
    public void remoteHeartBeat(Socket socket, Message<HeartBeat> message) throws IOException {

        Logger logger = LoggerFactory.getLogger(RctlServerHandler.class.getName() + ".remoteHeartBeat");

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
                SessionEstablish establish = SessionQueue.takeFirst(header.getId());
                if (establish != null) {
                    heartBeat.setAction("establish");
                    header.setSession(establish.getSession());
                }

                response(socket, new Message<>(header, heartBeat));// 将心跳反馈包发送给客户机
                Message<HeartBeat> request = receive(socket, HeartBeat.class);// 等待客户机发送新的心跳包

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

    private <T> Message<T> receive(Socket socket, Class<T> bodyClass) throws IOException {
        return new MessageResolver<T>(socket.getInputStream()).resolve(bodyClass);
    }

    private <T> void response(Socket socket, Message<T> message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        byte[] bytes = new MessageBuilder<>(message).build();
        outputStream.write(bytes);
        outputStream.flush();
    }
}
