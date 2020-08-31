package com.zjcoding.zrpserver.server.handler;

import com.zjcoding.zrpserver.common.protocol.ProxyMessage;
import com.zjcoding.zrpserver.web.service.LogService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * @Description 处理服务器接收到的外部请求
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 15:16
 */

@Component
public class RemoteHandler extends ChannelInboundHandlerAdapter {

    //以下代码为了解决自动注入对象为null的问题
    private static RemoteHandler remoteHandler;

    @Autowired
    private LogService logService;

    @PostConstruct
    public void init(){
        remoteHandler = this;
        remoteHandler.logService = this.logService;
    }

    private ServerHandler serverHandler = null;
    private int remotePort;
    private String clientKey;

    /**
     * @Description 不采用构造器赋值，因为SpringBoot会对构造器进行自动注入
     * @Date 14:16 2020/4/16
     * @Param [serverHandler, remotePort, clientKey]
     * @return void
     **/
    public void setValue(ServerHandler serverHandler, int remotePort,String clientKey) {
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;
        this.clientKey = clientKey;
    }

    //连接初始化，建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.TYPE_CONNECTED,ctx.channel().id().asLongText(),null);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口有请求进入，channelId为："+ctx.channel().id().asLongText());
    }

    //读取外部连接数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //从外部连接接收到的数据
        byte[] data = (byte[]) msg;
        //调用发送方法转发到客户端
        send(ProxyMessage.TYPE_DATA,ctx.channel().id().asLongText(),data);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口收到请求数据，数据量为"+data.length+"字节");
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.TYPE_DISCONNECTED,ctx.channel().id().asLongText(),null);
        System.out.println(this.getClass()+"\r\n"+remotePort+"端口有请求离开，channelId为："+ctx.channel().id().asLongText());
    }

    /**
     * @Description 发送数据到内网客户端流程封装
     * @Date 17:01 2020/2/20
     * @Param [type, channelId]
     * @return void
     **/
    public void send(int type,String channelId,byte[] data) {
        if (serverHandler==null){
            System.out.println(this.getClass()+"\r\n 客户端channel不存在");
            return;
        }
        ProxyMessage message = new ProxyMessage();
        message.setType(type);
        HashMap<String,Object> metaData = new HashMap<>();
        //每个请求都是一个channel，每个channel有唯一id，
        // 将该id发送至客户端，客户端返回响应时携带此id便可知道响应需要返回给哪个请求
        metaData.put("channelId",channelId);
        metaData.put("remotePort",remotePort);
        message.setMetaData(metaData);
        if (data!=null){
            message.setData(data);
        }
        this.serverHandler.getCtx().writeAndFlush(message);
        //记录日志，只记录数据传输请求
        if (remoteHandler!=null && ProxyMessage.TYPE_DATA==type){  //防止外部请求断开造成remoteHandler空指针异常
            remoteHandler.logService.addLog(clientKey,((InetSocketAddress)serverHandler.getCtx().channel().localAddress()).getPort(),(double) message.getData().length/1024);
        }
    }
}
