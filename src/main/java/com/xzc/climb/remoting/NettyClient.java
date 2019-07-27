package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient extends  AbstractClient {

    private  Channel channel;

    private  EventLoopGroup group ;

    public ClimberRespose send(ClimberRequest request){


        return null;
    }

    @Override
    public ClimberRespose send(ClimberRequest request, String address, InvokerConfig invokerConfig) {
        this.config = invokerConfig;
        String[] arr = address.split(":");
        ClimberRespose respose =null;
        String ip = arr[0];
        String port= arr[1];
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        try {
            this.group = new NioEventLoopGroup();
            Bootstrap bootstrap =new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NettyEncoder(config.getSerializer()))
                                    .addLast(new NettyDecoder(config.getSerializer(),ClimberRespose.class))
                                    .addLast(nettyClientHandler);
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
             this.channel = bootstrap.connect(ip, Integer.parseInt(port)).sync().channel();
             channel.writeAndFlush(request);
             respose  = nettyClientHandler.getRespose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return respose;
    }
}
