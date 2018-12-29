package sm.tools.rctl.base.module.net.serialize;

import java.nio.charset.Charset;

public interface Serializer<T> {

    byte[] serialize(T object, Charset charset);

    T deserialize(byte[] bytes, Charset charset);
}
