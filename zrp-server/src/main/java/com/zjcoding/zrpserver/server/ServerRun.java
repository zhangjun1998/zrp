package com.zjcoding.zrpserver.server;

import com.zjcoding.zrpserver.common.codec.ProxyMessageDecoder;
import com.zjcoding.zrpserver.common.codec.ProxyMessageEncoder;
import com.zjcoding.zrpserver.common.config.ConfigParser;
import com.zjcoding.zrpserver.server.handler.HeartBeatHandler;
import com.zjcoding.zrpserver.server.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @Description 代理服务器启动入口
 * @Author zhangjun
 * @Data 2020/2/19
 * @Time 16:52
 */

@Component
public class ServerRun {

    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    private static final int READER_IDLE_TIME = 40;
    private static final int WRITER_IDLE_TIME = 0;
    private static final int ALL_IDLE_TIME = 0;

    public void start()throws Exception{
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort  = (Integer) ConfigParser.get("server-port");
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(
                        //设置读超时为40秒，配合心跳机制处理器使用
                        new IdleStateHandler(READER_IDLE_TIME,WRITER_IDLE_TIME,ALL_IDLE_TIME, TimeUnit.SECONDS),
                        //固定帧长解码器
                        new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP),
                        //自定义协议解码器
                        new ProxyMessageDecoder(),
                        //自定义协议编码器
                        new ProxyMessageEncoder(),
                        //代理客户端连接代理服务器处理器
                        new ServerHandler(),
                        //服务器心跳机制处理器
                        new HeartBeatHandler()
                );
            }
        };

        ServerBootStrapHelper serverBootStrapHelper = new ServerBootStrapHelper();
        serverBootStrapHelper.bootStart(bossGroup,workerGroup,serverHost,serverPort,channelInitializer);
    }

}
