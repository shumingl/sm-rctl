package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;
import sm.tools.rctl.base.module.net.annotation.RctlEntity;

@RctlEntity(7)
public class RespMsg {

    public static class RESULT {
        public static final int SUCCEED = 0;
        public static final int FAILED = 1;
    }

    @FieldOrder(0)
    private int result;

    @FieldOrder(1)
    private String message;

    public RespMsg() {
    }

    public RespMsg(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
