package com.zjcoding.zrp.client.handler;

import com.zjcoding.zrp.client.common.config.ConfigParser;
import com.zjcoding.zrp.client.common.protocol.ProxyMessage;
import com.zjcoding.zrp.client.helper.ClientBootStrapHelper;
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
 * @Description 服务器连接处理器
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:15
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 全局管理channels
     */
    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //serverPort与localPort映射map
    private ConcurrentHashMap<Integer,Integer> portMap = new ConcurrentHashMap<>();
    //所有localChannel共享，减少线程上下文切换
    private EventLoopGroup localGroup = new NioEventLoopGroup();
    //每个外部请求channelId与其处理器handler的映射关系
    private ConcurrentHashMap<String,LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    private ChannelHandlerContext ctx = null;
    public ChannelHandlerContext getCtx() {
        return ctx;
    }
    
    //连接建立，初始化
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
        System.out.println(this.getClass()+"\r\n 与服务器连接建立成功，正在进行注册...");
    }

    //读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessage message = (ProxyMessage) msg;
        switch (message.getType()){
            // 授权
            case ProxyMessage.TYPE_AUTH :
                processAuth(message);
                break;
            // 外部请求进入，开始与内网建立连接
            case ProxyMessage.TYPE_CONNECTED :
                processConnected(message);
                break;
            // 断开连接
            case ProxyMessage.TYPE_DISCONNECTED :
                processDisConnected(message);
                break;
            // 心跳请求
            case ProxyMessage.TYPE_KEEPALIVE :
                //心跳，不做处理
                break;
            // 数据传输
            case ProxyMessage.TYPE_DATA :
                processData(message);
                break;
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.close();
        System.out.println(this.getClass()+"\r\n 与服务器连接断开");
        localGroup.shutdownGracefully();
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(this.getClass()+"\r\n 连接异常");
        cause.printStackTrace();
        //传递异常
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }

    /**
     * @Description 授权结果处理
     * @Date 11:37 2020/2/22
     * @Param [message]
     * @return void
     **/
    public void processAuth(ProxyMessage message){
        if ((Boolean) message.getMetaData().get("isSuccess")){
            System.out.println(this.getClass()+"\r\n 注册成功");
        }else {
            ctx.fireExceptionCaught(new Throwable());
            ctx.channel().close();
            System.out.println(this.getClass()+"\r\n 注册失败，原因："+message.getMetaData().get("reason"));
        }
    }

    /**
     * @Description 服务器通知客户端与本地服务建立连接
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
        System.out.println(this.getClass()+"\r\n 服务器"+remotePort+"端口进入连接，正在向本地"+localPort+"端口建立连接");
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
     * @Description 处理服务器传输的请求数据
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
            //将数据转发到对应内网服务器
            localHandler.getLocalCtx().writeAndFlush(message.getData());
        }
        System.out.println(this.getClass()+"\r\n 收到服务器数据，数据量为"+message.getData().length+"字节");
    }

}
