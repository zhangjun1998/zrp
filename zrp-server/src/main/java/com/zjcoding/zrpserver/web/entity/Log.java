package com.zjcoding.zrpserver.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Description 日志
 * @Author zhangjun
 * @Data 2020/4/15
 * @Time 16:03
 */

public class Log {

    private String clientKey;
    private String clientName;
    private int port;
    private long flow;
    @JsonFormat(pattern = "yyyy-MMM-dd",timezone = "GMT")
    private Date date;

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Log{" +
                "clientKey='" + clientKey + '\'' +
                ", clientName='" + clientName + '\'' +
                ", port=" + port +
                ", flow=" + flow +
                ", date=" + date +
                '}';
    }
}
