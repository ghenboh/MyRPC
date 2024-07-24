package org.example.serialize;

public interface Serializer {
    public int getCode();
    public byte[] serialize(Object obj);
    public Object deserialize(byte[] bytes, Class clazz);
    static Serializer getInstanceByCode(int code) {
        Serializer serializer = null;
        switch (code) {
            case 0:
                serializer = new KryoSerializer();
                break;
                case 1:
                    break;
            default:
                break;
        }
        return serializer;
    }
}
