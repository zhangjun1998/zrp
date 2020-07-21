package com.zjcoding.zrp.client.handler;

import com.zjcoding.zrp.client.common.config.ConfigParser;
import com.zjcoding.zrp.client.common.protocol.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * @Description 实际内网服务器channel连接处理器
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:15
 */

public class LocalHandler extends ChannelInboundHandlerAdapter {

    private ClientHandler clientHandler = null;
    private String remoteChannelId = null;
    private ChannelHandlerContext localCtx;

    public LocalHandler(ClientHandler clientHandler, String channelId) {
        this.clientHandler = clientHandler;
        this.remoteChannelId = channelId;
    }

    public ChannelHandlerContext getLocalCtx() {
        return localCtx;
    }

    //连接建立初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.localCtx = ctx;
        System.out.println(this.getClass()+"\r\n 与本地端口建立连接成功："+ctx.channel().remoteAddress());
    }

    //读取内网服务器请求和数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        ProxyMessage message = new ProxyMessage();
        message.setType(ProxyMessage.TYPE_DATA);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        metaData.put("clientKey", ConfigParser.get("client-key"));
        message.setMetaData(metaData);
        message.setData(data);
        //收到内网服务器响应后返回给服务器端
        this.clientHandler.getCtx().writeAndFlush(message);
        System.out.println(this.getClass()+"\r\n 收到本地"+ctx.channel().remoteAddress()+"的数据，数据量为"+data.length+"字节");
    }

    //连接断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyMessage message = new ProxyMessage();
        message.setType(ProxyMessage.TYPE_DISCONNECTED);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        message.setMetaData(metaData);
        this.clientHandler.getCtx().writeAndFlush(message);
        System.out.println(this.getClass()+"\r\n 与本地连接断开："+ctx.channel().remoteAddress());
    }

    //连接异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println(this.getClass()+"\r\n 连接中断");
        cause.printStackTrace();
    }
}
