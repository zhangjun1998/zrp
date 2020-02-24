package com.zjcoding.client.handler;

import com.zjcoding.client.ClientBootStrapHelper;
import com.zjcoding.zrp.common.config.ConfigParser;
import com.zjcoding.zrp.common.protocol.ProxyMessage;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author zhangjun
 * @Data 2020/2/22
 * @Time 11:06
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    //全局管理channels
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //serverPort与localPort映射map
    private static ConcurrentHashMap<Integer,Integer> portMap = new ConcurrentHashMap<>();
    //所有localChannel共享，减少线程上下文切换
    private static final EventLoopGroup localGroup = new NioEventLoopGroup();
    //每个外部请求channelId与其处理器handler的映射关系
    private static final ConcurrentHashMap<String,LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx = null;
    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //连接建立成功，发送注册请求
        ProxyMessage message = new ProxyMessage();
        message.setType(ProxyMessage.TYPE_REGISTER);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("clientKey", ConfigParser.get("client-key"));
        //获取配置中指定的服务器端口
        ArrayList<Integer> serverPortArr = new ArrayList<>();
        for (Map<String,Object> item : ConfigParser.getPortArray()){
            serverPortArr.add((Integer) item.get("server-port"));
            //保存端口映射关系
            portMap.put((Integer) item.get("server-port"),(Integer) item.get("client-port"));
        }
        metaData.put("ports",serverPortArr);
        message.setMetaData(metaData);
        ctx.writeAndFlush(message);
        System.out.println("与服务器连接建立成功，正在进行注册...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessage message = (ProxyMessage) msg;
        switch (message.getType()){
            case ProxyMessage.TYPE_AUTH :
                processAuth(message);
                break;
            case ProxyMessage.TYPE_CONNECTED :
                processConnected(message);
                break;
            case ProxyMessage.TYPE_DISCONNECTED :
                processDisConnected(message);
                break;
            case ProxyMessage.TYPE_KEEPALIVE :
                //心跳，不做处理
                break;
            case ProxyMessage.TYPE_DATA :
                processData(message);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.close();
        System.out.println("与服务器连接断开");
    }

    /**
     * @Description 授权结果处理
     * @Date 11:37 2020/2/22
     * @Param [message]
     * @return void
     **/
    public void processAuth(ProxyMessage message){
        if ((Boolean) message.getMetaData().get("isSuccess")){
            System.out.println("注册成功");
        }else {
            ctx.close();
            System.out.println("注册失败");
        }
    }

    /**
     * @Description 处理外部请求与代理服务器建立连接通知
     * @Date 11:38 2020/2/22
     * @Param [message]
     * @return void
     **/
    public void processConnected(ProxyMessage message){
        ClientHandler clientHandler = this;
        ClientBootStrapHelper localHelper = new ClientBootStrapHelper();
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                LocalHandler localHandler = new LocalHandler(clientHandler,message.getMetaData().get("channelId").toString());
                channel.pipeline().addLast(
                        new ByteArrayEncoder(),
                        new ByteArrayDecoder(),
                        localHandler
                );
                channels.add(channel);
                localHandlerMap.put(message.getMetaData().get("channelId").toString(),localHandler);
            }
        };
        String localhost = (String) ConfigParser.get("local-host");
        //这里根据portMap将远程服务器端口作为key获取对应的本地端口
        int remotePort = (Integer) message.getMetaData().get("remotePort");
        int localPort = portMap.get(remotePort);
        localHelper.start(localGroup,channelInitializer,localhost,localPort);
        System.out.println("服务器"+remotePort+"端口进入连接，正在向本地"+localPort+"端口建立连接");
    }

    /**
     * @Description 处理外部请求与代理服务器断开连接通知
     * @Date 11:40 2020/2/22
     * @Param [message]
     * @return void
     **/
    public void processDisConnected(ProxyMessage message){
        String channelId = message.getMetaData().get("channelId").toString();
        LocalHandler handler = localHandlerMap.get(channelId);
        if (handler!=null){
            handler.getLocalCtx().close();
            localHandlerMap.remove(channelId);
        }
    }

    /**
     * @Description 处理服务器传输的请求
     * @Date 11:40 2020/2/22
     * @Param [message]
     * @return void
     **/
    public void processData(ProxyMessage message){
        if (message.getData()==null || message.getData().length<=0){
            return;
        }
        String channelId = message.getMetaData().get("channelId").toString();
        LocalHandler localHandler = localHandlerMap.get(channelId);
        if (localHandler!=null){
            localHandler.getLocalCtx().writeAndFlush(message.getData());
        }
        System.out.println("收到服务器数据，数据量为"+message.getData().length+"字节");
    }

}
