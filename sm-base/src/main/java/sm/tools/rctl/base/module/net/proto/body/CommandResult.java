package sm.tools.rctl.base.module.net.proto.body;

import sm.tools.rctl.base.module.net.annotation.FieldOrder;

public class CommandResult {
    @FieldOrder(0)
    private int exitCode;
    @FieldOrder(1)
    private String stdOutput;
    @FieldOrder(2)
    private String errOutput;
    @FieldOrder(3)
    private boolean terminated;

    public CommandResult() {
    }

    public CommandResult(int code, String output, boolean terminated) {
        this(code, output);
        this.terminated = terminated;
    }

    public CommandResult(int code, String output) {
        this.exitCode = code;
        if (code == 0)
            this.stdOutput = output;
        else
            this.errOutput = output;
    }

    public boolean isSucceed() {
        return exitCode == 0;
    }

    public static CommandResult SUCCEED(String output) {
        CommandResult result = new CommandResult();
        result.setExitCode(0);
        result.setStdOutput(output);
        result.setTerminated(false);
        return result;
    }

    public static CommandResult FAILED(String output) {
        CommandResult result = new CommandResult();
        result.setExitCode(-1);
        result.setStdOutput(output);
        result.setTerminated(false);
        return result;
    }

    public boolean getTerminated1() {
        return terminated;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
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
