package com.ros.yang.rosservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ros.yang.rosservice.service.CommandCilent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosImageView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import rosjava_test_msgs.AddTwoIntsRequest;
import sensor_msgs.CompressedImage;

public class MainActivity extends RosActivity {

    private Button robot_test;
    private Button camera;
    private EditText command,flag;

    private String CAMERA_TOPIC = "/camera/image/compressed";

    private EditText req_a,req_b;
    Client client = new Client(MainActivity.this);
    CommandCilent commandCilent = new CommandCilent();
    private RosImageView<CompressedImage> cameraView;

    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(MainActivity.this,"命令执行失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this,"命令执行成功",Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
        }
    };

    public MainActivity() {
        super("RosService", "RosService");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_layout);
        command = findViewById(R.id.edit_command);
        flag = findViewById(R.id.edit_flag);
        camera = findViewById(R.id.btn_start_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag_num;
                String str_flag = flag.getText().toString();
                flag_num = Integer.valueOf(str_flag);
//                cameraClient.start_camera(is_admin,command.getText().toString());

                commandCilent.sendCommand(flag_num, command.getText().toString(), new CommandCilent.CommandClientListener() {
                    @Override
                    public void onSuccess(int result) {
                        Message msg = new Message();
                        msg.what = 1;
                        uiHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailse(int result) {
                        Message msg = new Message();
                        msg.what = 0;
                        uiHandler.sendMessage(msg);
                    }
                });
            }
        });
        cameraView = (RosImageView<CompressedImage>) findViewById(R.id.robot_main);
        cameraView.setMessageType(CompressedImage._TYPE);//message类型
        cameraView.setTopicName(CAMERA_TOPIC);//订阅的topic
        cameraView.setMessageToBitmapCallable(new MessageCallable<Bitmap, CompressedImage>() {
            @Override
            public Bitmap call(CompressedImage compressedImage) {
                ChannelBuffer buffer = compressedImage.getData();
                byte[] data = buffer.array();
                return BitmapFactory.decodeByteArray(data, buffer.arrayOffset(), buffer.readableBytes());
            }
        });
        robot_test = findViewById(R.id.robot_test);
        robot_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RobotMain.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void init(NodeMainExecutor nodeMainExecutor) {

        Server server = new Server();

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname(),getMasterUri());
        /**
         * 服务启动时，与节点不同.首先确保server是有效的，即启动成功的，才可以启动client,否则出现异常
         */
        nodeMainExecutor.execute(server,nodeConfiguration);

        nodeMainExecutor.execute(client,nodeConfiguration);
        nodeMainExecutor.execute(commandCilent,nodeConfiguration.setNodeName("main_command"));
        nodeMainExecutor.execute(cameraView, nodeConfiguration);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
