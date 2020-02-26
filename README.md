<h2>目录</h2>
<a href="#introduction">一、简介</a><br/>
<a href="#function">二、功能</a><br/>
<a href="#architecture">三、架构</a><br/>
<a href="#run">四、运行</a><br/>
<a href="#plan">五、计划</a><br/>


<h2 id="introduction">一、简介</h2>

zrp是使用Java开发的一款基于netty的内网穿透工具，主要用于将内网客户端反向代理到公网访问。目前支持所有tcp上层协议的代理，包括但不限于http、ssh、ftp等。

最初的开发目的是由于阿里云内存和CPU有限不方便部署一些个人项目，由于学校宽带运营商动态公网IP也不会给所以我写的DDNS也没有发挥用处。
观察市面上的一些反向代理应用后学习了反向代理技术的一些原理，本着挖掘技术和实际应用的想法开发了运用不同技术的代理工具。

<b>下面是我故意做的几个不同版本用来学习的，写了一些感受，和本项目使用无实际关系，可以跳过。</b>

**1.第一版**  
这个是为了加深对Http协议和Socket的使用而做的，只针对Http设计，不直接转发tcp流量，而是解析Http请求，重组后通过socket转发到内网，内网再返回响应。这种方式的缺点是灵活性太差，而且效率低下，创建的线程比较多，而且频繁的线程切换也会带来很多开销，由于BIO是阻塞的，虽然有线程池，但作用不是很大。优点是可以帮我了解Http协议和Socket的使用，因为我想实际对比一下BIO和NIO的区别，而不是只通过几个demo和网上的文字比较。

**2.第二版**  
第二版本来是准备直接使用netty的，不过感觉对NIO了解的不够深刻，而且对netty也不怎么了解。就准备通过学习Java NIO的方式去了解netty的一些原理，因此直接使用了Java NIO对第一版进行了重构，也不再只针对Http协议，而是直接转发TCP流量。Java NIO开始用起来不如BIO直观简单，但是熟悉后可以更加有效的去设计，这种事件驱动型的非阻塞IO模型在这种面对大量短连接的情况下发挥的非常出色，天生适用于高负载、高并发。而且NIO是面向缓冲区的，直接将数据读到缓冲区中操作，而BIO虽然也有Buffer这种包装类，但是本质上也还是流的包装类，需要从流读到缓冲区。

**3.第三版**  
第三版就是使用netty了，因为在第二版中我尝试了把一些东西给抽象出来封装成一个简单易用的小框架，实际操作的时候发现自己还是太嫩了。因此开始看netty的官方文档和github上的一些小demo，简单写了几个，感觉有点知其然不知其所以然，花两天读了《netty实战》这本书，基本明白了netty的思路和一些核心组件。然后借鉴了一些开源项目自己写了个内网穿透工具zrp。

<h2 id="function">二、功能</h2>
功能基本都是基于TCP的，支持TCP上层协议。大概使用场景有下面几个：
<br/>

- 内网web服务
- 内网ssh服务
- 内网ftp服务
- 内网数据库服务
- 远程桌面
- ...

<h2 id="architecture">三、架构</h2>
内网穿透流程基本都差不多，下图是一个简略的描述。

![zrp架构](https://github.com/zhangjun1998/zrp/raw/master/images/architecture.png)

<h2 id="run">四、运行</h2>
想要运行zrp很简单，只需要提供好zrp的配置文件即可。代理服务器与代理客户端的配置文件命名都是proxy-config.yaml。

下面是一个基本的代理服务器配置：
```
# 已授权客户端client-key
clients:
  - kas19kn#fkhDKAsadhk
  - KAHSKKASdhkajsk211a
  - jas#HFKfkkhakkdajdL
```
下面是代理客户端配置：
```
# 代理客户端配置
# 
# 代理服务器host(我这里直接用localhost做的测试，后面会重测)
server-host: xx.xx.xx.xx
# 代理服务器数据传输port
server-port: 8888
# 本地服务器host
local-host: 127.0.0.1
# 代理客户端认证密钥client-key
client-key: kas19kn#fkhDKAsadhk
# server-port与local-port映射配置
config:
  # server-port：代理服务器外部访问端口
  # client-port：代理客户端实际使用端口
  # proxy-type： 采用的代理模式，目前只支持tcp
  #
  # 3306端口，一般是mysql代理
  - server-port: 9906
    client-port: 3306
    proxy-type: tcp
  #
  # 8080端口，一般是http代理
  # 这里也直接转发tcp流量，速度会较快
  - server-port: 9980
    client-port: 8080
    proxy-type: tcp
  #
  # 22端口，一般是ssh代理
  - server-port: 9922
    client-port: 22
    proxy-type: tcp
  #
  # 21端口，一般是ftp代理
  - server-port: 9921
    client-port: 21
    proxy-type: tcp
```
配置完成后运行SererRun和ClientRun即可。

成功运行后会看到类似下面的一些提示信息：  
代理服务器：
```
有客户端建立连接，客户端地址为：/127.0.0.1:64839
客户端注册成功，clientKey为：kas19kn#fkhDKAsadhk
9922端口有请求进入，channelId为：00155dfffe890110-00001e0c-00000006-01807925852f9261-84d0470f
9922端口收到请求数据，数据量为28字节
收到客户端返回数据，数据量为33字节
```
代理客户端：
```
与服务器连接建立成功，正在进行注册...
注册成功
服务器9922端口进入连接，正在向本地22端口建立连接
与本地端口建立连接成功：/127.0.0.1:22
收到服务器数据，数据量为28字节
收到本地/127.0.0.1:22的数据，数据量为33字节
```
下面这两张图是ssh和mysql连接的测试：

![ssh截图](https://github.com/zhangjun1998/zrp/blob/master/images/ssh.png)
![mysql截图](https://github.com/zhangjun1998/zrp/blob/master/images/mysql.png)

<h2 id="plan">五、计划</h2>

- [ ] 实现对Https的支持，方便调试微信小程序。
- [ ] 开发一个仪表盘，方便在线管理。
- [ ] 数据压缩，我的阿里云带宽小，减少传输的数据量。
- [ ] 使用redis在代理服务器上缓存一些静态web资源，减少重复请求。
- [ ] 负载均衡。
