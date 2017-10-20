package command_executive;

import org.ros.internal.message.Message;

/**
 * Created by yang on 17-10-17.
 */

public interface CommandDataType extends Message {
    String _TYPE = "command_executive/CommandDataType";
    String _DEFINITION = "int64 flag\n" +
            "string command\n" +
            "---\n" +
            "int64 result";
}
