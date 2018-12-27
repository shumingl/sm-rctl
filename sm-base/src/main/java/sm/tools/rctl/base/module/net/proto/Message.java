package sm.tools.rctl.base.module.net.proto;

import java.io.Serializable;

public class Message<T> implements Serializable {
    private static final long serialVersionUID = -154852945163596901L;
    private Header header;
    private T body;

    public Message() {
    }

    public Message(Header header, T body) {
        this.header = header;
        this.body = body;
    }

    public Message withHeader(Header header) {
        this.header = header;
        return this;
    }

    public Message withBody(T body) {
        this.body = body;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
