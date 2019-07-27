package com.xzc.climb.remoting;

import com.xzc.climb.config.invoker.InvokerConfig;
import com.xzc.climb.utils.ClimberException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResposeFuture  implements Future<ClimberRespose> {

    private ClimberRequest request;
    private InvokerConfig config;
    private ClimberRespose respose;
    private boolean isDone;
    public ResposeFuture(ClimberRequest request, InvokerConfig config){


        this.config=config;
        this.request= request;
        config.setResposeFuture(request.getId(),this);
    }
    public void notifyResponse(ClimberRespose respose){
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
