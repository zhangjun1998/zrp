package com.zjcoding.client;

import com.zjcoding.client.handler.ClientHandler;
import com.zjcoding.zrp.common.codec.ProxyMessageDecoder;
import com.zjcoding.zrp.common.codec.ProxyMessageEncoder;
import com.zjcoding.zrp.common.config.ConfigParser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @Description 客户端启动入口
 * @Author zhangjun
 * @Data 2020/2/20
 * @Time 17:16
 */

public class ClientRun {

    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void main(String[] args){
        new ClientRun().start();
    }

    public void start(){
        ClientBootStrapHelper clientBootStrapHelper = new ClientBootStrapHelper();
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort = (Integer) ConfigParser.get("server-port");
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP),
                        new ProxyMessageDecoder(),
                        new ProxyMessageEncoder(),
                        new ClientHandler()
                );
            }
        };
        clientBootStrapHelper.start(workerGroup,channelInitializer,serverHost,serverPort);
    }

}
