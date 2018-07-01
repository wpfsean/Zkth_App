package com.zhketech.client.zkth.app.project.pagers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zhketech.client.zkth.app.project.R;
import com.zhketech.client.zkth.app.project.base.BaseActivity;
import com.zhketech.client.zkth.app.project.beans.SipBean;
import com.zhketech.client.zkth.app.project.global.AppConfig;
import com.zhketech.client.zkth.app.project.rtsp.RtspServer;
import com.zhketech.client.zkth.app.project.rtsp.media.VideoMediaCodec;
import com.zhketech.client.zkth.app.project.rtsp.media.h264data;
import com.zhketech.client.zkth.app.project.rtsp.record.Constant;
import com.zhketech.client.zkth.app.project.taking.tils.Linphone;
import com.zhketech.client.zkth.app.project.taking.tils.PhoneCallback;
import com.zhketech.client.zkth.app.project.utils.Logutils;
import org.linphone.core.LinphoneCall;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

/**
 * 播打电话界面
 */


public class SingleCallActivity extends BaseActivity implements View.OnClickListener, Camera.PreviewCallback, SurfaceHolder.Callback {


    @BindView(R.id.secodary_surfacevie)
    public SurfaceView secodary_surfacevie;

    @BindView(R.id.main_progressbar)
    public ProgressBar main_progressbar;
    @BindView(R.id.secondary_progressbar)
    public ProgressBar secondary_progressbar;

    @BindView(R.id.show_call_time)
    public TextView show_call_time;

    @BindView(R.id.btn_handup_icon)
    public ImageButton hangupButton;

    @BindView(R.id.btn_mute)
    public ImageButton btn_mute;

    @BindView(R.id.btn_volumeadd)
    public ImageButton btn_volumeadd;

    @BindView(R.id.btn_camera)
    public ImageButton btn_camera;

    @BindView(R.id.btn_volumelow)
    public ImageButton btn_volumelow;
    //显示网络状态
    @BindView(R.id.icon_network)
    public ImageView network_pic;


    @BindView(R.id.main_player_framelayout)
    public FrameLayout main_player_framelayout;

    @BindView(R.id.second_player_relativelayout)
    public RelativeLayout second_player_relativelayout;

    @BindView(R.id.video_relativelayout)
    public RelativeLayout video_relativelayout;

    @BindView(R.id.text_who_is_calling_information)
    public TextView text_who_is_calling_information;


    //显示电量
    @BindView(R.id.icon_electritity_show)
    public ImageView icon_electritity_information;
    List<SipBean> sipListResources = new ArrayList<>();
    AudioManager mAudioManager = null;
    Context mContext;
    NodePlayer nodePlayer;
    boolean isCall = true;//来源是打电话还是接电话，true为打电话，false为接电话
    String userName = "wpf";
    boolean isVideo = false;
    String rtsp = "";//可视电话的视频地址
    boolean isSilent = false;//是否静音
    private Boolean isCallConnected = false;//是否已接通
    boolean mWorking = false;
    //计时的子线程
    Thread mThread = null;
    int num = 0;
    String native_name = "";
    NodePlayerView np;

    //视频录制的硬编码参数
    private static int queuesize = 10;
    public static ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<>(queuesize);
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<>(queuesize);
    private RtspServer mRtspServer;
    private String RtspAddress;
    private SurfaceHolder surfaceHolder;//小窗口
    private Camera mCamera;
    private VideoMediaCodec mVideoMediaCodec;
    // private AvcEncoder avcEncoder;
    private boolean isRecording = false;
    private static int cameraId = 0;//默认后置摄像头
    boolean isBandService = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    num++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_call_time.setText(getTime(num) + "");
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public int intiLayout() {
        return R.layout.activity_single_call;
    }

    @Override
    public void initData() {

        native_name = AppConfig.native_sip_name;
        isCall = this.getIntent().getBooleanExtra("isCall", true);//是打电话还是接电话
        userName = this.getIntent().getStringExtra("userName");//对方号码
        isVideo = this.getIntent().getBooleanExtra("isVideo", false);//是可视频电话，还是语音电话



        sipListResources = new ArrayList<>();
        String result = "";

        Logutils.i("UserName:" + userName);
        Logutils.i("native_name:" + native_name);
        Logutils.i("isCall:" + isCall);
        Logutils.i("isVideo:" + isVideo);
        //电话监听回调
        phoneCallback();

        //判断来电是否已接通
        boolean isConnet =  this.getIntent().getBooleanExtra("isCallConnected", false);
        if (isConnet){runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show_call_time.setText("00:00");
                hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                text_who_is_calling_information.setText("正在与" + userName + "通话");
                threadStart();
            }
        });}
        //向外播放电话
        if (isCall) {
            Linphone.callTo(userName, false);
        }
        //视频电话向外播打电话
        if (isCall && isVideo){
            text_who_is_calling_information.setVisibility(View.GONE);
            main_player_framelayout.setVisibility(View.VISIBLE);
            second_player_relativelayout.setVisibility(View.VISIBLE);

            nodePlayer.setInputUrl("rtmp://live.hkstv.hk.lxdns.com/live/hks");
            nodePlayer.setAudioEnable(false);
            nodePlayer.start();
        }

    }

    private void phoneCallback() {

        Linphone.addCallback(null, new PhoneCallback() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                Logutils.i("来电");
            }

            @Override
            public void outgoingInit() {
                Logutils.i("outgoingInit");

                isCallConnected = true;
                if (isCallConnected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_call_time.setText("00:00");
                            hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                            text_who_is_calling_information.setText("正在响铃《  " + userName + "  》");
                        }
                    });
                }
            }

            @Override
            public void callConnected() {
                Logutils.i("callConnected");
                if (isCallConnected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_call_time.setText("00:00");
                            hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                            text_who_is_calling_information.setText("正在与" + userName + "通话");
                            threadStart();
                        }
                    });
                }

            }

            @Override
            public void callEnd() {
                Logutils.i("callEnd");
            }

            @Override
            public void callReleased() {
                Logutils.i("callReleased");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show_call_time.setText("00:00");
                        hangupButton.setBackgroundResource(R.drawable.btn_answer_select);
                    }
                });
                if (nodePlayer != null) {
                    nodePlayer.pause();
                    nodePlayer.stop();
                    nodePlayer.release();
                    nodePlayer = null;
                }
                SingleCallActivity.this.finish();
            }

            @Override
            public void error() {
                Logutils.i("error");
            }
        });


    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        nodePlayer = new NodePlayer(this);
        np = (NodePlayerView) findViewById(R.id.main_view);
        np.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        // np.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        nodePlayer.setPlayerView(np);

        hangupButton.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_volumelow.setOnClickListener(this);
        secodary_surfacevie.setZOrderOnTop(true);
        btn_mute.setOnClickListener(this);
        btn_volumeadd.setOnClickListener(this);

        surfaceHolder = secodary_surfacevie.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        surfaceHolder.setKeepScreenOn(true);

        RtspAddress = "rtsp://" + AppConfig.current_ip + ":" + RtspServer.DEFAULT_RTSP_PORT;
        mVideoMediaCodec = new VideoMediaCodec();
        if (RtspAddress != null) {
            // SharedPreferencesUtils.putObject(mContext,AppConfig.NATIVE_RTSP,RtspAddress);
            Log.i("tag", "地址: " + RtspAddress);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  bindService(intent, mRtspServiceConnection, Context.BIND_AUTO_CREATE);

        // if (isCall){  unbindService(mRtspServiceConnection);}
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_handup_icon:

                if (isCallConnected) {
                    Linphone.hangUp();
                }

                break;
            case R.id.btn_mute:
                if (!isSilent) {
                    Linphone.toggleMicro(true);
                    btn_mute.setBackgroundResource(R.mipmap.btn_mute_pressed);
                    isSilent = true;
                } else {
                    Linphone.toggleMicro(false);
                    btn_mute.setBackgroundResource(R.mipmap.btn_voicetube_pressed);
                    isSilent = false;
                }
                break;
            //前后摄像头的转换
            case R.id.btn_camera:

                if (cameraId == 0) {
                    cameraId = 1;
                } else {
                    cameraId = 0;
                }
                initCamera();
                try {
                    play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_volumeadd:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                break;
            //音量减
            case R.id.btn_volumelow:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nodePlayer != null) {
            if (nodePlayer.isPlaying()) {
                nodePlayer.pause();
                nodePlayer.stop();
                nodePlayer.release();
                nodePlayer = null;
            }
        }
    }


    /**
     * 计时线程开启
     */
    public void threadStart() {
        mWorking = true;
        if (mThread != null && mThread.isAlive()) {
            Logutils.i("start: thread is alive");
        } else {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mWorking) {
                        try {
                            Thread.sleep(1 * 1000);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            mThread.start();
        }
    }

    /**
     * 计时线程停止
     */
    public void threadStop() {
        if (mWorking) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
                mThread = null;
            }
            show_call_time.setText("00:00");
            mWorking = false;
        }
    }

    /**
     * int转成时间 00:00
     */
    public static String getTime(int num) {
        if (num < 10) {
            return "00:0" + num;
        }
        if (num < 60) {
            return "00:" + num;
        }
        if (num < 3600) {
            int minute = num / 60;
            num = num - minute * 60;
            if (minute < 10) {
                if (num < 10) {
                    return "0" + minute + ":0" + num;
                }
                return "0" + minute + ":" + num;
            }
            if (num < 10) {
                return minute + ":0" + num;
            }
            return minute + ":" + num;
        }
        int hour = num / 3600;
        int minute = (num - hour * 3600) / 60;
        num = num - hour * 3600 - minute * 60;
        if (hour < 10) {
            if (minute < 10) {
                if (num < 10) {
                    return "0" + hour + ":0" + minute + ":0" + num;
                }
                return "0" + hour + ":0" + minute + ":" + num;
            }
            if (num < 10) {
                return "0" + hour + minute + ":0" + num;
            }
            return "0" + hour + minute + ":" + num;
        }
        if (minute < 10) {
            if (num < 10) {
                return hour + ":0" + minute + ":0" + num;
            }
            return hour + ":0" + minute + ":" + num;
        }
        if (num < 10) {
            return hour + minute + ":0" + num;
        }
        return hour + minute + ":" + num;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //前后摄像头的数据采集,根据前后进行相应的视频流旋转
//        Log.d("views","data:  "+data.length);
        if (cameraId == 0) {
            data = VideoMediaCodec.rotateYUVDegree90(data, Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        } else {
            data = VideoMediaCodec.rotateYUV420Degree270(data, Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        }
        putYUVData(data, data.length);

    }


    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }

    private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {

        @Override
        public void onError(RtspServer server, Exception e, int error) {
            // We alert the user that the port is already used by another app.
            if (error == RtspServer.ERROR_BIND_FAILED) {
                new AlertDialog.Builder(SingleCallActivity.this)
                        .setTitle("Port already in use !")
                        .setMessage("You need to choose another port for the RTSP server !")
                        .show();
            }
        }


        @Override
        public void onMessage(RtspServer server, int message) {
            if (message == RtspServer.MESSAGE_STREAMING_STARTED) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SingleCallActivity.this, "RTSP STREAM STARTED", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (message == RtspServer.MESSAGE_STREAMING_STOPPED) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SingleCallActivity.this, "RTSP STREAM STOPPED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    private ServiceConnection mRtspServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRtspServer = ((RtspServer.LocalBinder) service).getService();
            mRtspServer.addCallbackListener(mRtspCallbackListener);
            mRtspServer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
        try {
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /**
     * put数据
     *
     * @param buffer
     * @param type
     * @param ts
     */
    public static void putData(byte[] buffer, int type, long ts) {
        if (h264Queue.size() >= queuesize) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        data.type = type;
        data.ts = ts;
        h264Queue.add(data);
    }

    /**
     * 初始化相机参数
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mVideoMediaCodec.prepare();
        mVideoMediaCodec.isRun(true);
        try {
            mCamera = Camera.open(cameraId);
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera.setDisplayOrientation(0);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode("off");
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewFrameRate(15);
        parameters.setPreviewSize(Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Logutils.e("setParameters failed");
        }
        mCamera.setPreviewCallback(this);
    }

    private void play() throws IOException {
        mCamera.startPreview();
        if (RtspAddress != null && !RtspAddress.isEmpty()) {
            isRecording = true;
            Intent intent = new Intent(this, RtspServer.class);
            bindService(intent, mRtspServiceConnection, Context.BIND_AUTO_CREATE);
            isBandService = true;
        }
        new Thread() {
            @Override
            public void run() {
                mVideoMediaCodec.getBuffers();
            }
        }.start();
    }


}
