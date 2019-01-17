package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;
import sm.tools.rctl.base.module.net.annotation.RctlEntity;

@RctlEntity(2)
public class HeartBeat {
    @FieldOrder(0)
    private long seq;
    @FieldOrder(1)
    private String action;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
