package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class HeartBeat {
    @FieldOrder(0)
    private long seq;

    public HeartBeat() {
    }

    public HeartBeat(long seq) {
        this.seq = seq;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
