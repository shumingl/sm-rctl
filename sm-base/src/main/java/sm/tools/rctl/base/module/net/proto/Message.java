package sm.tools.rctl.base.module.net.proto;

import java.io.Serializable;

public class Message<T> implements Serializable {
    private static final long serialVersionUID = -154852945163596901L;
    private Header header;
    private T body;

    public Message(Header header, T body) {
        this.header = header;
        this.body = body;
    }

    public Message<T> withBytes(byte[] bytes) {
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public T getBody() {
        return body;
    }

}
