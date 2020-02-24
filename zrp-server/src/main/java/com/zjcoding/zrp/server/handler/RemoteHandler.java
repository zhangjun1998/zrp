package com.zjcoding.zrp.server.handler;

import com.zjcoding.zrp.common.protocol.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * @Description 处理服务器接收到的外部请求
 * @Author zhangjun
 * @Data 2020/2/20
 * @Time 15:42
 */

public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private ServerHandler serverHandler = null;
    private int remotePort;

    public RemoteHandler(ServerHandler serverHandler, int remotePort) {
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.TYPE_CONNECTED,ctx.channel().id().asLongText(),null);
        System.out.println(remotePort+"端口有请求进入，channelId为："+ctx.channel().id().asLongText());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        send(ProxyMessage.TYPE_DISCONNECTED,ctx.channel().id().asLongText(),null);
        System.out.println(remotePort+"端口有请求离开，channelId为："+ctx.channel().id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        send(ProxyMessage.TYPE_DATA,ctx.channel().id().asLongText(),data);
        System.out.println(remotePort+"端口收到请求数据，数据量为"+data.length+"字节");
    }

    /**
     * @Description 发送数据流程封装
     * @Date 17:01 2020/2/20
     * @Param [type, channelId]
     * @return void
     **/
    public void send(int type,String channelId,byte[] data){
        if (serverHandler==null){
            System.out.println("客户端channel不存在");
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
    }
}
