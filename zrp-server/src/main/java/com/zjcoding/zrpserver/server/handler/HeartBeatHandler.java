package com.zjcoding.zrpserver.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * @Description 服务器端心跳机制处理器
 * @Author zhangjun
 * @Data 2020/5/22
 * @Time 20:02
 */

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    //读超时是指在指定时间内没有接收到任何数据
    //写超时是指在指定时间内没有发送任何数据
    //将服务器端的读超时设置为60秒，客户端写超时设为50秒，客户端写超时则发送一个心跳包
    //因此服务器端与客户端如果正常连接则一定不会发生读超时
    //如果发生读超时则可能是网络延迟太高或者断线了，这时候就可以考虑断开连接了

    //读超时次数，超过指定次数则表示客户端离线或者网络不稳定，主动断开客户端连接
    private int waitCount = 1;
    private static final int MAX_WAIT_COUNT = 3;

    //连续超时间隔，80秒
    private static final long MAX_TIME_MILLIS_LIMIT = 60*1000;

    //上次读超时的时间，只有在一定时间范围内连续超时达到最大超时次数才表示客户端掉线
    //否则重置超时次数
    private long lastDisConnectTimeMillis = 0;

    //上次超时时间
    private long currentMillis = 0;

    //服务器读超时事件发生时会默认调用该方法
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        currentMillis = System.currentTimeMillis();

        //两次超时时间间隔过长，重置超时次数与最后一次超时时间
        if ((currentMillis-lastDisConnectTimeMillis)>MAX_TIME_MILLIS_LIMIT){
            if (waitCount==1){
                waitCount+=1;
                System.out.println(this.getClass()+"\r\n 首次超时");
            }else {
                waitCount=2;
                System.out.println(this.getClass()+"\r\n 已重置超时次数与最后一次超时时间");
            }
            lastDisConnectTimeMillis = currentMillis;
        } else if (waitCount>=MAX_WAIT_COUNT){
            ctx.channel().close();
            System.out.println(this.getClass()+"\r\n 连续读超时次数达到3次，已主动断开与客户端的连接");
        }else {
            System.out.println(this.getClass()+"\r\n 读超时次数："+waitCount);
            waitCount+=1;
            lastDisConnectTimeMillis = currentMillis;
        }
    }

    //连接异常，出现异常时直接关闭客户端连接
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println(this.getClass()+"\r\n 客户端连接异常");
        cause.printStackTrace();
    }
}
