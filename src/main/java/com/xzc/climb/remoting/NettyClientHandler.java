package com.xzc.climb.remoting;

import com.xzc.climb.utils.ClimberException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.*;

public class NettyClientHandler extends SimpleChannelInboundHandler<ClimberRespose>  implements  Future<ClimberRespose
        > {


    private ClimberRespose respose;
    private boolean isDone;

    public NettyClientHandler (){
        super();
        isDone=false;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClimberRespose respose) throws Exception {
        notifyResponse(respose);
    }


    private void notifyResponse(ClimberRespose respose){
        synchronized (this){
            this.respose= respose;
            isDone=true;
            notifyAll();
        }
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
    @Override
    public ClimberRespose get()  {

        return get(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public ClimberRespose get(long timeout, TimeUnit unit) {
        if(!isDone){
            try {
                synchronized (this) {
                    if (timeout < 0) {
                        wait();
                    }else {
                        wait(timeout);

                        if (!isDone){
                            throw new TimeoutException("time out");
                        }
                    }

                }
            }catch (InterruptedException e) {
                throw new ClimberException(e);
            } catch (TimeoutException e) {
                throw new ClimberException(e);
            }

        }
        isDone=false;
        return respose;
    }
}
