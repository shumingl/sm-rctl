package sm.tools.rctl.server.core.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class ReturnMessage {

    @FieldOrder(0)
    private int result;

    @FieldOrder(1)
    private String message;

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
