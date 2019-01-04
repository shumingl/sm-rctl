package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class Command {

    @FieldOrder(0)
    private String command;

    public Command() {
    }

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String toString() {
        return command;
    }
}
