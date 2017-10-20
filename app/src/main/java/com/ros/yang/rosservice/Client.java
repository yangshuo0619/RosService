package com.ros.yang.rosservice;

import android.content.Context;
import android.util.Log;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import rosjava_test_msgs.AddTwoInts;
import rosjava_test_msgs.AddTwoIntsResponse;


/**
 * Created by yang on 17-9-13.
 */

public class Client extends AbstractNodeMain {

    private Context activity;
    private ConnectedNode connectedNode =null;
    private ServiceClient<rosjava_test_msgs.AddTwoIntsRequest, rosjava_test_msgs.AddTwoIntsResponse> addClient = null;

    private int A = 0;
    private int B = 0;
    public Client(Context context){
        activity = context;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("add_two_ints/client");
    }

    /**
     * 递归等待，until server is working
     */
    private void createClient() {
        if (connectedNode != null) {
            try {
                addClient = connectedNode.newServiceClient("add_two_ints", AddTwoInts._TYPE);

            } catch (ServiceNotFoundException e) {
                Log.i("yang", "it is sleep 1 second,the reason is the service is not working");
                try {
                    Thread.sleep(1000L);
                    createClient();
                } catch (Exception ex) {
                    Log.e(getClass().getName(),"client is error ,the reason is "+ex.getMessage());
                }
            }
        }
    }

    public int add(int a,int b){
        int sum =0;

            if (addClient != null){
                final rosjava_test_msgs.AddTwoIntsRequest request = addClient.newMessage();
                Log.i("yang","add class "+request.getClass());
                Log.i("yang","toString "+request.toString());
                request.setA(a);
                request.setB(b);
                addClient.call(request, new ServiceResponseListener<AddTwoIntsResponse>(){

                    @Override
                    public void onSuccess(AddTwoIntsResponse addTwoIntsResponse) {

                        Log.i("yang","client is ok."+"sum is "+addTwoIntsResponse.getSum());
                    }

                    @Override
                    public void onFailure(RemoteException e) {
                        Log.i("yang","add is fail");
                    }
                });

            }
        return sum;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        this.connectedNode = connectedNode;
        createClient();
//        add(A,B);

    }



}

