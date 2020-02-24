package com.zjcoding.client.handler;

import com.zjcoding.zrp.common.protocol.ProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;

/**
 * @Description 实际服务器channel连接处理器
 * @Author zhangjun
 * @Data 2020/2/22
 * @Time 12:00
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.localCtx = ctx;
        System.out.println("与本地端口建立连接成功："+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        ProxyMessage message = new ProxyMessage();
        message.setType(ProxyMessage.TYPE_DATA);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        message.setMetaData(metaData);
        message.setData(data);
        this.clientHandler.getCtx().writeAndFlush(message);
        System.out.println("收到本地"+ctx.channel().remoteAddress()+"的数据，数据量为"+data.length+"字节");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyMessage message = new ProxyMessage();
        message.setType(ProxyMessage.TYPE_DISCONNECTED);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        message.setMetaData(metaData);
        this.clientHandler.getCtx().writeAndFlush(message);
        System.out.println("与本地连接断开："+ctx.channel().remoteAddress());
    }
}
