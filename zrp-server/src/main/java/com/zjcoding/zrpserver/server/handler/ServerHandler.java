package com.zjcoding.zrpserver.server.handler;

import com.zjcoding.zrpserver.common.config.ConfigParser;
import com.zjcoding.zrpserver.common.protocol.ProxyMessage;
import com.zjcoding.zrpserver.server.ServerBootStrapHelper;
import com.zjcoding.zrpserver.web.service.ClientService;
import com.zjcoding.zrpserver.web.service.LogService;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description 处理服务器接收到的客户端连接
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 15:16
 */

@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ClientService clientService;
    @Autowired
    private LogService logService;

//    // 防止clientService注入失败
    private static ServerHandler serverHandler;

    @PostConstruct
    public void init(){
        serverHandler = this;
        serverHandler.clientService = this.clientService;
        serverHandler.logService = this.logService;
    }

    //在所有ServerHandler中共享当前在线的授权信息
    private static ArrayList<String> clients = new ArrayList<>();

    //统一管理客户端channel和remote channel
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //所有remote channel共享的线程池，减少线程创建
    //bossGroup指的是所有指定端口的监听处理线程
    //workerGroup指的是所有端口所收到连接的处理线程
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ServerBootStrapHelper remoteHelper = new ServerBootStrapHelper();

    //客户端标识clientKey
    private String clientKey;
    //代理客户端的ChannelHandlerContext
    private ChannelHandlerContext ctx;
    //判断代理客户端是否已注册授权
    private boolean isRegister = false;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        System.out.println(this.getClass()+"\r\n 有客户端建立连接，客户端地址为："+ctx.channel().remoteAddress());
    }

    //数据读取与转发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyMessage message = (ProxyMessage) msg;
        if (message.getType() == ProxyMessage.TYPE_REGISTER){
            //处理客户端注册请求
            processRegister(message);
        }else if (isRegister){
            switch (message.getType()){
                //客户端请求断开连接
                case ProxyMessage.TYPE_DISCONNECTED :
                    processDisconnect(message);
                    break;
                //心跳，不做处理
                case ProxyMessage.TYPE_KEEPALIVE :
                    break;
                //处理数据
                case ProxyMessage.TYPE_DATA :
                    processData(message);
                    break;
            }
        }else {
            System.out.println(this.getClass()+"\r\n 有未授权的客户端尝试发送消息，断开连接");
            ctx.close();
        }
    }

    //连接中断
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        ctx.channel().close();
        //移除断开客户端的授权码
        clients.remove(clientKey);
        //取消正在监听的端口，否则第二次连接时无法再次绑定端口
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass()+"\r\n 客户端连接中断："+ctx.channel().remoteAddress());
    }

    //连接异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println(this.getClass()+"\r\n 连接异常，已中断");
        cause.printStackTrace();
    }

    /**
     * @Description 判断客户端是否有授权
     * @Date 18:04 2020/2/22
     * @Param [clientKey]
     * @return boolean
     **/
    public synchronized boolean isLegal(String clientKey){
        boolean flag = serverHandler.clientService.checkClientKey(clientKey);
//        boolean flag = serverHandler.clientService.checkClientKey(clientKey);
        if (flag){
            //一个client-key只允许一个代理客户端使用
            for (String client : clients){
                if (client.equals(clientKey)){
                    System.out.println(this.getClass()+"\r\n同一授权不允许重复登录");
                    return false;
                }
            }
            clients.add(clientKey);
            this.clientKey = clientKey;
            return true;
        }
        return false;
    }

    /**
     * @Description 处理客户端注册请求
     * @Date 16:00 2020/2/20
     * @Param [message]
     * @return void
     **/
    public void processRegister(ProxyMessage message) throws Exception{

        HashMap<String,Object> metaData = new HashMap<>();

        ServerHandler serverHandler = this;

        String clientKey = message.getMetaData().get("clientKey").toString();
        //客户端合法性判断
        if (isLegal(clientKey)){
            String host = (String) ConfigParser.get("server-host");
            //指定服务器需要开启的对外访问端口
            ArrayList<Integer> ports = (ArrayList<Integer>) message.getMetaData().get("ports");
            try {
                for (int port : ports){
                    ChannelInitializer channelInitializer = new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            RemoteHandler remoteHandler = new RemoteHandler();
                            remoteHandler.setValue(serverHandler,port,clientKey);
                            channel.pipeline().addLast(
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    remoteHandler
                            );
                            //向channelGroup注册remote channel
                            channels.add(channel);

                        }
                    };
                    remoteHelper.bootStart(bossGroup,workerGroup,host,port,channelInitializer);
                }
                metaData.put("isSuccess",true);
                isRegister = true;
                System.out.println(this.getClass()+"\r\n 客户端注册成功，clientKey为："+clientKey);
            }catch (Exception e){
                metaData.put("isSuccess",false);
                metaData.put("reason",e.getMessage());
                System.out.println(this.getClass()+"\r\n processRegister()中出现错误");
                System.out.println(this.getClass()+"\r\n 启动器出错，客户端注册失败，clientKey为："+clientKey);
                e.printStackTrace();
            }
        }else {
            metaData.put("isSuccess",false);
            metaData.put("reason","client-key不合法，请两分钟后重试");
            System.out.println(this.getClass()+"\r\n 客户端注册失败，使用了不合法的clientKey，clientKey为："+clientKey);
        }

        ProxyMessage res = new ProxyMessage();
        res.setType(ProxyMessage.TYPE_AUTH);
        res.setMetaData(metaData);
        ctx.writeAndFlush(res);
    }

    /**
     * @Description 处理客户端断开请求
     * @Date 16:25 2020/2/20
     * @Param [message]
     * @return void
     **/
    public void processDisconnect(ProxyMessage message){
        channels.close(new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                return channel.id().asLongText().equals(message.getMetaData().get("channelId"));
            }
        });
        System.out.println(this.getClass()+"\r\n 有客户端请求断开，clientKey为："+clientKey);
    }

    /**
     * @Description 处理客户端发送的数据
     * @Date 16:25 2020/2/20
     * @Param [message]
     * @return void
     **/
    public void processData(ProxyMessage message){
        if (message.getData()==null || message.getData().length<=0){
            return;
        }
        //根据channelId转发到channelGroup上注册的相应remote channel(外部请求)
        channels.writeAndFlush(message.getData(),new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                if (channel.id().asLongText().equals(message.getMetaData().get("channelId"))){
                    //添加日志
                    String clientKey = (String) message.getMetaData().get("clientKey");
                    serverHandler.logService.addLog(clientKey,((InetSocketAddress)channel.localAddress()).getPort(),(double) message.getData().length/1024);

                    return true;
                }
                return false;
            }
        });
        System.out.println(this.getClass()+"\r\n 收到客户端返回数据，数据量为"+message.getData().length+"字节");

    }

}
