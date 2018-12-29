package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class Command {

    @FieldOrder(0)
    private String base;

    @FieldOrder(1)
    private String command;

    public Command() {
    }

    public Command(String base, String command) {
        this.base = base;
        this.command = command;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
