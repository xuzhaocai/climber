package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;
import com.xzc.climb.utils.ClimberException;
import com.xzc.climb.utils.CommonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NettyClient extends  AbstractClient {

    private static final ConcurrentMap<String ,ChannalNode >  channals;
    static {
        channals =new ConcurrentHashMap<>();
    }
    public ClimberRespose send(ClimberRequest request){
        return null;
    }
    private  ChannalNode  initClientChannal(String address){
        ChannalNode node  =new ChannalNode();
        String[] ipArr = CommonUtil.parseIPAndPort(address);
        node.handler = new NettyClientHandler(config);
        try {
            node.group = new NioEventLoopGroup();
            Bootstrap bootstrap =new Bootstrap();
            bootstrap.group(node.group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NettyEncoder(config.getSerializer()))
                                    .addLast(new NettyDecoder(config.getSerializer(),ClimberRespose.class))
                                    .addLast(node.handler);
                        }
                    })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            node.channel = bootstrap.connect(ipArr[0], Integer.parseInt(ipArr[1])).sync().channel();
        } catch (InterruptedException e) {
            throw new ClimberException("init channal failed ");

        }
        channals.putIfAbsent(address, node);
        return node;
    }
    @Override
    public void send(ClimberRequest request, String address, InvokerConfig invokerConfig) {
        this.config = invokerConfig;
        ChannalNode channalNode = channals.get(address);
        if (channalNode==null){
            channalNode =initClientChannal(address);
        }
        Channel channel  = channalNode.channel;

        channel.writeAndFlush(request);
    }

    public static class ChannalNode{
        private  Channel channel;
        private  EventLoopGroup group ;
        private  NettyClientHandler handler;
    }

}
