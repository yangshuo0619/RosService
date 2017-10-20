package com.ros.yang.rosservice.service;

import android.util.Log;

import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import command_executive.CommandDataType;
import command_executive.CommandDataTypeRequest;
import command_executive.CommandDataTypeResponse;

/**
 * Created by yang on 17-10-17.
 */

public class CommandCilent extends AbstractNodeMain {

    private ServiceClient<CommandDataTypeRequest, CommandDataTypeResponse> client = null;
//    CommandClientListener listener;

    public CommandCilent(){

    }
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("command_client");
    }

    public int sendCommand(int flag, String command, final CommandClientListener listener){
        int result = 0;
        final CommandDataTypeRequest request = client.newMessage();
        request.setFlag(flag);
        request.setCommand(command);
        client.call(request, new ServiceResponseListener<CommandDataTypeResponse>() {
            @Override
            public void onSuccess(CommandDataTypeResponse commandDataTypeResponse) {
                listener.onSuccess((int) commandDataTypeResponse.getResult());
            }

            @Override
            public void onFailure(RemoteException e) {
                listener.onFailse(0);
                Log.e("mynt","meaage is "+e.getMessage());
            }
        });
        return result;
    }
    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        super.onStart(connectedNode);

        if (connectedNode != null) {
            try {

                client = connectedNode.newServiceClient("command", CommandDataType._TYPE);
            } catch (ServiceNotFoundException e) {
                e.printStackTrace();
            }
//            if (addTwoIntsClient != null) {
//                AddTwoIntsRequest request = addTwoIntsClient.newMessage();
//                request.setA(1);
//                request.setB(2);
//                addTwoIntsClient.call(request, new ServiceResponseListener<AddTwoIntsResponse>() {
//                    @Override
//                    public void onSuccess(AddTwoIntsResponse addTwoIntsResponse) {
//                        Log.i("rokey", "onSuccess the sum is " + addTwoIntsResponse.getSum());
//                    }
//
//                    @Override
//                    public void onFailure(RemoteException e) {
//                        Log.i("rokey", "call failure");
//                    }
//                });
//            }
        } else {
            Log.e("mynt", "connected is null");
        }
    }

    /**
     * 为命令客户端增加回调函数
     */
    public interface CommandClientListener{

        void onSuccess(int result);
        void onFailse(int result);
    }
}
