package sm.tools.rctl.base.module.net.serialize;

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.nio.charset.Charset;

public interface Serializer<T> {

    byte[] serialize(T object, Charset charset) throws SerializationException;

    T deserialize(byte[] bytes, Charset charset) throws DeserializationException;
}
