package command_executive;

import org.ros.internal.message.Message;

/**
 * Created by yang on 17-10-17.
 */

public interface CommandDataTypeRequest extends Message {
    String _TYPE = "command_executive/CommandDataTypeRequest";
    String _DEFINITION = "int64 flag\n" +
            "string command\n";

    void setFlag(long flag);
    long getFlag();

    void setCommand(String command);
    String getCommand();
}
