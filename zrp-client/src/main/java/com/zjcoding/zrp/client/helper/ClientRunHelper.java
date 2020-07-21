package com.zjcoding.zrp.client.helper;

import com.zjcoding.zrp.client.common.codec.ProxyMessageDecoder;
import com.zjcoding.zrp.client.common.codec.ProxyMessageEncoder;
import com.zjcoding.zrp.client.common.config.ConfigParser;
import com.zjcoding.zrp.client.handler.ClientHandler;
import com.zjcoding.zrp.client.handler.HeartBeatHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Description 运行帮助类
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:20
 */

public class ClientRunHelper {

    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    private static final int READER_IDLE_TIME = 0;
    private static final int WRITER_IDLE_TIME = 30;
    private static final int ALL_IDLE_TIME = 0;

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start(){
        ClientBootStrapHelper clientBootStrapHelper = new ClientBootStrapHelper();
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort = (Integer) ConfigParser.get("server-port");
        ChannelInitializer channelInitializer = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(
                        //设置写超时为30秒，配合心跳机制处理器使用
                        new IdleStateHandler(READER_IDLE_TIME,WRITER_IDLE_TIME,ALL_IDLE_TIME, TimeUnit.SECONDS),
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP),
                        new ProxyMessageDecoder(),
                        new ProxyMessageEncoder(),
                        new ClientHandler(),
                        //客户端心跳机制处理器
                        new HeartBeatHandler(workerGroup)
                );
            }
        };
        clientBootStrapHelper.start(workerGroup,channelInitializer,serverHost,serverPort);
    }

}
