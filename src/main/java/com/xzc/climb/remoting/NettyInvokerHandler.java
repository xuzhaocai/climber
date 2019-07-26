package com.xzc.climb.remoting;

import com.xzc.climb.config.provider.ProviderConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyInvokerHandler  extends SimpleChannelInboundHandler<ClimberRequest> {
    private ProviderConfig config;
    public NettyInvokerHandler (ProviderConfig config){
        this.config = config;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClimberRequest request) throws Exception {
        ClimberRespose respose = config.doInvoke(request);
        channelHandlerContext.writeAndFlush(respose);
    }
}
