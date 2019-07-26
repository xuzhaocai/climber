package com.xzc.climb.remoting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<ClimberRespose> {
    private  ClimberRespose respose;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClimberRespose respose) throws Exception {
        this.respose= respose;
    }

    public ClimberRespose getRespose() {
        return respose;
    }

    public void setRespose(ClimberRespose respose) {
        this.respose = respose;
    }
}
