package sm.tools.rctl.base.module.net.serialize;

import sm.tools.rctl.base.utils.ObjectUtil;

import java.nio.charset.Charset;

public class DefaultSerializer implements Serializer<Object> {
    @Override
    public byte[] serialize(Object object, Charset charset) {
        return ObjectUtil.getString(object).getBytes(charset);
    }

    @Override
    public Object deserialize(byte[] bytes, Charset charset) {
        String stringValue = new String(bytes, charset);
        return null;
    }
}
