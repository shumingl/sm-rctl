package sm.tools.rctl.base.module.net.proto;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = -154852945163596901L;
    private Header header;
    private Body body;

    public Message withHeader(Header header) {
        this.header = header;
        return this;
    }

    public Message withBody(Body body) {
        this.body = body;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }
}
