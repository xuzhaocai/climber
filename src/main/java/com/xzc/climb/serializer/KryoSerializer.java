package com.xzc.climb.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xzc.climb.serializer.Serializer;
import com.xzc.climb.utils.ClimberException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer  implements Serializer {



    private ThreadLocal<Kryo> threadLocal= new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);

            return kryo;
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
            byte[] bytes = outputStream.toByteArray();
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





}
