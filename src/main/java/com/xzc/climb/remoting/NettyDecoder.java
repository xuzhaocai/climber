package com.xzc.climb.remoting;

import com.xzc.climb.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {
    private Serializer serializer;
    private Class<?> clazz;
    public NettyDecoder(Serializer serializer , Class clazz){
        this.clazz=clazz;
        this.serializer =serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = serializer.decode(bytes, clazz);
        list.add(obj);
    }
}