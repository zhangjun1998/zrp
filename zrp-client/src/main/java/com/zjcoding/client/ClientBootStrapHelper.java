package com.zjcoding.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description 代理客户端启动器帮助类
 * @Author zhangjun
 * @Data 2020/2/20
 * @Time 17:19
 */

public class ClientBootStrapHelper {

    private Channel channel = null;

    public synchronized void start(EventLoopGroup workerGroup, ChannelInitializer channelInitializer,
                      String host, int port){

        if (host==null || port==0){
            System.out.println("配置信息有误");
            return;
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(channelInitializer);


            channel = bootstrap.connect(host,port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
                channel.deregister();
                channel.close();
            });
        }catch (Exception e){
            workerGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }

    public synchronized void close(){
        channel.close();
    }

}
