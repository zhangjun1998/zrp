package com.zjcoding.zrpserver.common.codec;

import com.zjcoding.zrpserver.common.protocol.ProxyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description ProxyMessage解码器，需要先经过LengthFieldBasedFrameDecoder解码
 * @Author zhangjun
 * @Data 2020/4/14
 * @Time 15:14
 */

public class ProxyMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int type = byteBuf.readInt();

        int metaDataLength = byteBuf.readInt();
        CharSequence metaDataString =byteBuf.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String,Object> metaData = jsonObject.toMap();

        byte[] data = null;
        if (byteBuf.isReadable()){
            data = ByteBufUtil.getBytes(byteBuf);
        }

        ProxyMessage proxyMessage = new ProxyMessage();
        proxyMessage.setType(type);
        proxyMessage.setMetaData(metaData);
        proxyMessage.setData(data);

        list.add(proxyMessage);
    }
}
