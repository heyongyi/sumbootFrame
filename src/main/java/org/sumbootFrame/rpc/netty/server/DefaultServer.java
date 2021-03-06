package org.sumbootFrame.rpc.netty.server;

import io.netty.util.concurrent.GlobalEventExecutor;
import org.sumbootFrame.rpc.netty.common.SocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * Created by thinkpad on 2018/2/24.
 */
public class DefaultServer {
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//ImmediateEventExecutor

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
//                .childHandler(new DefaultServerInitializer(channelGroup))
                .childHandler(new SocketServerInitializer(channelGroup))
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_RCVBUF, 10 * 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
        return future;
    }


    public void destroy() {
//        if(channel != null) {
//            channel.close();
//        }
        channelGroup.close();
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
