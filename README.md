# zrp

![Hex.pm](https://img.shields.io/hexpm/l/ok)
![Maven Central](https://img.shields.io/badge/maven-3.6.0-blue.svg)
![](https://img.shields.io/badge/Java-1.8-green.svg)
![](https://img.shields.io/badge/zrp-0.0.2-yellow.svg)

zrp 是使用Java开发的一款基于netty的内网穿透工具，主要用于将内网服务反向代理到公网访问。目前支持所有TCP上层协议的代理，包括但不限于HTTP、SSH、FTP、TELNET、SMTP、POP3、DNS等。

## 目录

* [开发背景](#开发背景)
* [基本原理](#基本原理)
* [功能说明](#功能说明)
* [使用示例](#使用示例)
    * [下载](#下载)
    * [配置](#配置)
    * [运行](#运行)
    * [自测截图](#自测截图)
* [计划](#计划)
* [联系我](#联系我)
* [致谢](#致谢)
* [许可](#许可)

## 开发背景

**这对你来说也许是一个漫长的无聊故事，不想看就跳过吧......**

用官话讲就是由于xx问题，产生xx现象，导致普罗大众都没办法xxx，因此xx技术应运而生。

以上这些在我开发完之前对我来说都是扯淡，事实上我一开始没有想做一个内网穿透工具的想法，在大三上学期(2018年)的时候我已经能够做出一些自以为很有意思的东西(实际上是些垃圾)，然而没有办法让更多人看到就少了一些成就感，所以我就开始准备白嫖，从NATAPP嫖到向日葵，最后打电话想嫖运营商的公网IP，也许是我当时的白嫖技术不行，导致本地服务在网络上嫖到失联。

不得已我掏出了大招，在当时一年前用学生价嫖到的阿里云服务器，1核2G+40G磁盘，部署我的一些垃圾玩具够用了。但还是很不爽，每次开发完打包部署真特么麻烦，直接本地开服务访问多舒服，而且我还想搞一波远程桌面等其它有意思的东西，阿里云用起来还是差了点意思。

后来我知道运营商给我们的宽带有一些竟然是有动态公网IP的，我就做了一个DDNS工具，配上我在阿里注册的域名，万事俱备，就差一个动态公网IP了，测了一下家里、学校的宽带竟然都没有？？？懵了，卒。

再后面读大四又参加了互联网+竞赛，做的是一个物联网项目，不过当时没有那么多时间去考虑就直接用了阿里的物联网平台，后来我了解到Ngrok、frp，恰好我的阿里云还在续费，理所当然我就...没有用这两个软件，因为我看了一下它们实现内网穿透的原理，感觉问题不大，想自己造一个。后面就是去全球最大同性交友网站找了一下frp的源码，发现是Go写的，当时我就想一定要用Java写一个，等有空了我学一下Go再写一个。然后又扒了lanproxy的源码，最后就写了这个frp的十八线山寨版zrp。

最后就是顺便用zrp当作了师生合作类型的毕业设计题目，第一次碰到毕业设计指导老师会给学生钱，师生一起商量怎么才能避免评上优秀毕业设计(但凡有一粒花生米也不会喝成这样)，虽然最后还是没有成功逃掉，但是回头想想也没有我想象的那么麻烦，再次给那位李姓指导老师致谢，师傅领进门，修行靠自身。

## 基本原理

基本原理就很简单了，两个字就懂了，转发。看下面这个图吧，想深入了解细节处理建议看一下frp的源码，千万不要看我的代码，会误人子弟。

![基本原理](https://github.com/zhangjun1998/zrp/raw/master/images/architecture.png)

## 功能说明

目前来说zrp本身没有什么附加功能，只是做了内网穿透，在文末Todo里面会列出zrp后面会做出的修改与完善。下面是可以使用zrp做的一些事情：
+ 内网web服务
+ 使用FTP、SSH等远程连接内网计算机
+ windows远程桌面
+ ......任意不违反法律的操作

## 使用示例

### 下载

1. 代理服务器端zrp-server：根据源码自行打包
2. 代理客户端zrp-client下载：[zrp-client](https://github.com/zhangjun1998/zrp/releases/download/0.0.2/zrp-client.jar)

### 配置

1. 代理服务器配置文件：[zrp-server.yaml](https://github.com/zhangjun1998/zrp/blob/master/zrp-server.yaml)
<a href="https://github.com/zhangjun1998/zrp/blob/master/zrp-server.yaml">下载zrp-server.yaml</a>
```
# 代理服务器配置

# host
server-host: 0.0.0.0

# 代理服务器数据传输port
server-port: 10987
```

2. 初始化数据库：[zrp.sql](https://github.com/zhangjun1998/zrp/blob/master/zrp.sql)

3. 代理客户端配置文件：[zrp-client.yaml](https://github.com/zhangjun1998/zrp/blob/master/zrp-client.yaml)
```
# 代理客户端配置

# 代理服务器host
server-host: 这里填你代理服务器的公网IP

# 代理服务器数据传输port，需要与zrp-server.yaml配置一致
server-port: 10987

# 本地服务器host
local-host: 127.0.0.1

# 代理客户端认证密钥client-key
client-key: your key

# server-port与local-port映射配置
config:
  # server-port：代理服务器外部访问端口
  # client-port：代理客户端实际使用端口
  # proxy-type： 采用的代理模式
  # description：代理描述
  #
  - server-port: 9980
    client-port: 8080
    proxy-type: tcp
    description: http代理
  #
  - server-port: 9922
    client-port: 22
    proxy-type: tcp
    description: ssh代理
  #
  - server-port: 9921
    client-port: 21
    proxy-type: tcp
    description: ftp代理
  #
  - server-port: 9989
    client-port: 3389
    proxy-type: tcp
    description: 远程桌面代理
```

### 运行
1. zrp-server运行
```
# 打包zrp-server源码为jar文件
# 将zrp-server.yaml与zrp-server.jar放在同级目录
# 运行以下命令(需配置好Java环境)
java -jar zrp-server.jar
```
2. zrp-client运行
```
# 进入zrp-client所在目录
# 将zrp-client.yaml与zrp-client放在同级目录
# 运行以下命令
java -jar zrp-client.jar
```

### 自测截图
1. 连接内网数据库

![内网数据库连接演示](https://github.com/zhangjun1998/zrp/raw/master/images/mysql.png)

2. ssh连接内网计算机

![ssh连接演示](https://github.com/zhangjun1998/zrp/raw/master/images/ssh.png)

## 计划

- [x] 管理员仪表盘，在线管理代理客户端
- [ ] 数据压缩，节约带宽，但会增大CPU负载
- [ ] 端口复用，允许不同协议使用同一端口
- [ ] TCP多路复用，减少TCP连接数

## 联系我

+ 邮箱：zhangjun_java@163.com
+ 微信：rzy_zj
+ Github提issue

## 致谢

+ <span style="color:#03a9f4">我亲爱的女朋友<span/>
+ <span style="color:#03a9f4">我的大学老师李某<span/>
+ [frp](https://github.com/fatedier/frp)
+ [lanproxy](https://github.com/ffay/lanproxy)

## 许可

![Hex.pm](https://img.shields.io/hexpm/l/ok)

未经允许，请勿以任何方式借此牟利。
