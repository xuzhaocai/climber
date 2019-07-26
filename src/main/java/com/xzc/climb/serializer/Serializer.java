package com.xzc.climb.serializer;

import java.io.IOException;

public interface Serializer {
    byte[] encode(Object msg) ;
    <T> T decode(byte[] buf, Class<T> type);
}
