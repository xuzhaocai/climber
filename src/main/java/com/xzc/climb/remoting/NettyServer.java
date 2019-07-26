package com.xzc.climb.remoting;

import com.xzc.climb.serializer.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer   extends   AbstractServer{
    private Thread thread;
    private Serializer serializer ;
    public void start(int port) {
        thread= new Thread(()->{
            EventLoopGroup  boss= new NioEventLoopGroup(1);
            EventLoopGroup  worker= new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .childOption(ChannelOption.SO_KEEPALIVE,true)
                        .childHandler(new ChannelInitializer<NioSocketChannel>() {
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline()
                                        .addLast(new NettyEncoder(serializer))
                                        .addLast(new NettyDecoder(serializer,ClimberRequest.class))
                                        .addLast(new NettyInvokerHandler());

                            }
                        });
                ChannelFuture future = bootstrap.bind(port);
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
    }
}
