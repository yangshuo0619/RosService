package command_executive;

import org.ros.internal.message.Message;

/**
 * Created by yang on 17-10-17.
 */

public interface CommandDataTypeResponse extends Message {
    String _TYPE = "command_executive/CommandDataTypeResponse";
    String _DEFINITION = "int64 result";
    void setResult(long result);
    long getResult();
}
