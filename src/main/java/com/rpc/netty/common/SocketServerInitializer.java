package com.rpc.netty.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by thinkpad on 2018/2/24.
 */
public class SocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelGroup group;

    public SocketServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 设置30秒没有读到数据，则触发一个READER_IDLE事件。
        // pipeline.addLast(new IdleStateHandler(30, 0, 0));
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        ch.pipeline().addLast("http-codec",new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        // ChunkedWriteHandler：向客户端发送HTML5文件
        ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        // 在管道中添加我们自己的接收数据实现方法
        ch.pipeline().addLast("handler",new SelfWebSocketServerHandler(group));

    }
}
