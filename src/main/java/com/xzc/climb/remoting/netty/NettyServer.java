package com.xzc.climb.remoting.netty;

import com.xzc.climb.config.provider.ProviderConfig;
import com.xzc.climb.remoting.AbstractServer;
import com.xzc.climb.remoting.ClimberRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer   extends AbstractServer {
    private Thread thread;

    public void start(int port, ProviderConfig config) {
        this.config= config;
        thread= new Thread(()->{
            EventLoopGroup  boss= new NioEventLoopGroup(1);
            EventLoopGroup  worker= new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childOption(ChannelOption.SO_KEEPALIVE,true)
                        .childOption(ChannelOption.TCP_NODELAY,true)
                        .childHandler(new ChannelInitializer<NioSocketChannel>() {
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline()
                                        .addLast(new NettyEncoder(config.getSerializer()))
                                        .addLast(new NettyDecoder(config.getSerializer(), ClimberRequest.class))
                                        .addLast(new NettyInvokerHandler(config));
                            }
                        });
                ChannelFuture future = bootstrap.bind(port);
                // 执行注册
                doStart();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    public void stop() {
        if (thread!=null && thread.isAlive()){
            thread.interrupt();
        }
        onStop();
    }
}
