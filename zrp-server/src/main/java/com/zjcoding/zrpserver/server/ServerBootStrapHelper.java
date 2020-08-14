package com.zjcoding.zrpserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @Description 代理服务器启动器帮助类
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 15:17
 */

@Component
public class ServerBootStrapHelper {

    /**
     * @Description 启动监听
     * @Date 15:27 2020/2/20
     * @Param [bossGroup, workerGroup, port, channelInitializer]
     * @return void
     **/
    public synchronized void bootStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                       String serverHost, int serverPort, ChannelInitializer channelInitializer) throws Exception{
        try {
            ServerBootstrap serverBootstrap = new  ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
                    //连接超时时间，连接公网服务器时可能会超时导致连接失败，最好不要设置
                    //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
            Channel channel = serverBootstrap.bind(serverHost,serverPort).sync().channel();
            //Channel channel = serverBootstrap.bind(serverHost,serverPort).awaitUninterruptibly().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
                //channel关闭，将channel从workerGroup取消注册
                channel.deregister();
                channel.close();
            });
        }catch (Exception e){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println(this.getClass()+"\r\n bootStart()中出现错误");
            e.printStackTrace();
        }
    }



}
