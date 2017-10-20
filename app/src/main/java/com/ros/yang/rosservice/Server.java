package com.ros.yang.rosservice;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;
import org.ros.node.service.ServiceServerListener;

import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;

/**
 * Created by yang on 17-9-13.
 */

public class Server extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("add_two_ints/server");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        ServiceServer serviceServer = connectedNode.newServiceServer("add_two_ints", rosjava_test_msgs.AddTwoInts._TYPE,
                new ServiceResponseBuilder<AddTwoIntsRequest, AddTwoIntsResponse>() {
                    @Override
                    public void
                    build(rosjava_test_msgs.AddTwoIntsRequest request, rosjava_test_msgs.AddTwoIntsResponse response) {
                        response.setSum(request.getA() + request.getB());
                    }
                });
        serviceServer.addListener(new ServiceServerListener() {
            @Override
            public void onShutdown(ServiceServer serviceServer) {

            }

            @Override
            public void onMasterRegistrationSuccess(Object o) {

            }

            @Override
            public void onMasterRegistrationFailure(Object o) {

            }

            @Override
            public void onMasterUnregistrationSuccess(Object o) {

            }

            @Override
            public void onMasterUnregistrationFailure(Object o) {

            }
        });
        Log.i("yang","server is ok");

    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);

    }
}
