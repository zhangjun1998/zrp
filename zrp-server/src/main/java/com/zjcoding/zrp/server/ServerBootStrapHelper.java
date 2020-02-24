package com.zjcoding.zrp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description 代理服务器启动器帮助类
 * @Author zhangjun
 * @Data 2020/2/20
 * @Time 15:12
 */

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
            Channel channel = serverBootstrap.bind(serverHost,serverPort).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
                //channel关闭，将channel从workerGroup取消注册
                channel.deregister();
                channel.close();
            });
        }catch (Exception e){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }



}
