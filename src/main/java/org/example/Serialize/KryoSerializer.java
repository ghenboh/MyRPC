package org.example.Serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        Kryo kryo = new Kryo();
        kryo.register(obj.getClass());
        kryo.setRegistrationRequired(false);
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeObject(output, obj);
        return output.toBytes();
    }

    public Object deserialize(byte[] bytes, Class clazz) {
        Kryo kryo = new Kryo();
        kryo.register(clazz);
        kryo.setRegistrationRequired(false);
        System.out.println("Serialized bytes length: " + bytes.length);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input input = new Input(bais);
        return kryo.readObject(input, clazz);
    }

    @Override
    public int getCode() {
        return 0;
    }
}
