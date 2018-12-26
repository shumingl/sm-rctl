package sm.tools.rctl.base.module.net.serialize;

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

public interface Serializer<T> {

    byte[] serialize(T object) throws SerializationException;

    T deserialize(byte[] bytes) throws DeserializationException;
}
