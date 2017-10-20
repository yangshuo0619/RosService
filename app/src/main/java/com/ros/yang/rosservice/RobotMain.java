package com.ros.yang.rosservice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ros.yang.rosservice.service.CommandCilent;

import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosImageView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import sensor_msgs.CompressedImage;

public class RobotMain extends RosActivity implements View.OnClickListener{

    Button start,check,stop_check,power;
    CommandCilent commandCilent = new CommandCilent();

    private String CAMERA_TOPIC = "/camera/rgb/image_raw/compressed";
    private RosImageView<CompressedImage> cameraView;

    public RobotMain() {
        super("RosService", "RosService");
    }

    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(RobotMain.this,"命令执行失败",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(RobotMain.this,"命令执行成功",Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
    }

    private void initView() {
        start = findViewById(R.id.btn_start);
        check = findViewById(R.id.btn_check);
        stop_check = findViewById(R.id.btn_stop_check);
        power = findViewById(R.id.btn_power);

        start.setOnClickListener(this);
        check.setOnClickListener(this);
        stop_check.setOnClickListener(this);
        power.setOnClickListener(this);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname(),getMasterUri());
        nodeMainExecutor.execute(commandCilent,nodeConfiguration.setNodeName("robot_test_command"));
        nodeMainExecutor.execute(cameraView, nodeConfiguration.setNodeName("robot_test_camera"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_start:
                start_robot();
                break;
            case R.id.btn_check:
                check_point();
                break;
            case R.id.btn_stop_check:
                stop_check_point();
                break;
            case R.id.btn_power:
                auto_power();
                break;
            default:
                break;
        }

    }

    private void start_robot() {
        commandCilent.sendCommand(0, "roslaunch command_executive demo.launch", new CommandCilent.CommandClientListener() {
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

    private void check_point() {
        commandCilent.sendCommand(0, "rosrun turtlebot_checkpoints turtlebot_checkpoits.py", new CommandCilent.CommandClientListener() {
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

    private void stop_check_point() {
        commandCilent.sendCommand(0, "rosnode kill /nav_test" +
                "", new CommandCilent.CommandClientListener() {
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

    private void auto_power() {
        commandCilent.sendCommand(0, "rosrun turtlebot_checkpoints autodocking.py", new CommandCilent.CommandClientListener() {
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
}
