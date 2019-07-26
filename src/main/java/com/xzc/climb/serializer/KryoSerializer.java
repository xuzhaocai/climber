package com.xzc.climb.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.ClimberException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KryoSerializer  implements Serializer {



    private ThreadLocal<Kryo> threadLocal= new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue() {
            return createKryo();
        }
    };

    private Kryo  getKryo(){
        return threadLocal.get();
    }
    @Override
    public byte[] encode(Object msg)  {

        Kryo kryo = getKryo();
        ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
        Output  output = new Output(outputStream);
        try {
            kryo.writeObject(output, msg);
            output.flush();
            byte[] bytes = output.toBytes();
            return bytes;
        }catch (Exception e){
            throw new ClimberException(e);
        }finally {
            try {
                output.close();
            }catch (Exception e1){
                throw  new ClimberException(e1);

            }
            try {
                outputStream.close();
            } catch (IOException e) {
                throw  new ClimberException(e);
            }
        }
    }

    @Override
    public <T> T decode(byte[] buf, Class<T> type)  {
        Kryo kryo = getKryo();
        ByteArrayInputStream inputStream= new ByteArrayInputStream(buf);
        Input input=new Input(inputStream);
        try {
            T object = kryo.readObject(input, type);
            return object;
        }catch (Exception e){
            throw  new ClimberException(e);
        }finally {
            input.close();
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private  Kryo createKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        kryo.register(String[].class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);
        return kryo;
    }
}
