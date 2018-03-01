package com.rpc.netty.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by thinkpad on 2018/2/24.
 */
public class DefaultServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public DefaultServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //处理日志
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        //处理心跳
        pipeline.addLast(new IdleStateHandler(0, 0, 1800, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());

        //处理http请求
        pipeline.addLast("decoder", new HttpRequestDecoder()) ;  // 1
        pipeline.addLast("encoder", new HttpResponseEncoder()) ; // 2
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024))  ;  // 3
        pipeline.addLast("handler", new HttpRequestHandler());        // 4

    }
}
