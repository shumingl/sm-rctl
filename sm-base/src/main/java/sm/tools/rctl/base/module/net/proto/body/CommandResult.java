package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class CommandResult {
    @FieldOrder(0)
    private int exitCode;
    @FieldOrder(1)
    private String stdOutput;
    @FieldOrder(2)
    private String errOutput;

    public boolean isSucceed() {
        return exitCode == 0;
    }

    public static CommandResult SUCCEED(String output) {
        CommandResult result = new CommandResult();
        result.setExitCode(0);
        result.setStdOutput(output);
        return result;
    }

    public static CommandResult FAILED(String output) {
        CommandResult result = new CommandResult();
        result.setExitCode(-1);
        result.setStdOutput(output);
        return result;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStdOutput() {
        return stdOutput;
    }

    public void setStdOutput(String stdOutput) {
        this.stdOutput = stdOutput;
    }

    public String getErrOutput() {
        return errOutput;
    }

    public void setErrOutput(String errOutput) {
        this.errOutput = errOutput;
    }
}
