package com.zjcoding.zrp.client.common.protocol;

import java.util.Arrays;
import java.util.Map;

/**
 * @Description 自定义协议
 * @Author zhangjun
 * @Data 2020/5/26
 * @Time 21:18
 */

public class ProxyMessage {

    //消息类型
    private int type;
    //元数据
    private Map<String,Object> metaData;
    //消息内容
    private byte[] data;

    //注册
    public static final int TYPE_REGISTER = 1;
    //授权
    public static final int TYPE_AUTH = 2;
    //建立连接
    public static final int TYPE_CONNECTED = 3;
    //断开连接
    public static final int TYPE_DISCONNECTED = 4;
    //心跳
    public static final int TYPE_KEEPALIVE = 5;
    //数据传输
    public static final int TYPE_DATA = 6;

    public ProxyMessage() {

    }

    public ProxyMessage(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProxyMessage{" +
                "type=" + type +
                ", metaData=" + metaData +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
