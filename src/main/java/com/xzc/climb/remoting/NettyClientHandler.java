package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;
import com.xzc.climb.utils.ClimberException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.*;

public class NettyClientHandler extends SimpleChannelInboundHandler<ClimberRespose>   {
    private InvokerConfig config;
    public NettyClientHandler (InvokerConfig config){
        this.config =config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClimberRespose respose) throws Exception {
        config.notifyResposeFuture(respose);
    }



}
