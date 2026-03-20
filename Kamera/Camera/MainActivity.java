package com.example.polinav3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.example.polinav3.AI_prediction.Recognition;
import com.example.polinav3.gamepad.ButtonInput;
import com.example.polinav3.gamepad.ButtonType;
import com.example.polinav3.gamepad.GameControllerService;
import com.example.polinav3.gamepad.LocalGamepadConnector;
import com.example.polinav3.gamepad.NetworkGamepadConnector;

import com.example.polinav3.video.CameraHandlerThread;
import com.example.polinav3.video.MediaStreamManager;
import com.example.polinav3.video.VisionMediaDecoder;
import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.ErrorCode;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.beans.OperationResult;
import com.sanbot.opensdk.function.unit.HDCameraManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.ProjectorManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TopBaseActivity implements SurfaceHolder.Callback {
    ProjectorManager projectorManager = (ProjectorManager) getUnitManager(FuncConstant.PROJECTOR_MANAGER);
    HardWareManager hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);

    //Camera
    private HDCameraManager hdCameraManager;
    private MediaStreamManager mediaStreamManager;
    private SurfaceView sv;
    private VisionMediaDecoder mediaDecoder;
    private CameraHandlerThread cameraTread;

    SystemManager systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);

    private Handler handler = new Handler();

    public HardWareManager getHardWareManager() {
        return hardWareManager;
    }

    private int streamId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Log.d("testo", "oncreate");
        register(MainActivity.class);
        setContentView(R.layout.activity_main);

        cameraTread = new CameraHandlerThread();


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        hdCameraManager = (HDCameraManager) getUnitManager(FuncConstant.HDCAMERA_MANAGER);
        mediaDecoder = new VisionMediaDecoder();
        mediaStreamManager = new MediaStreamManager(hdCameraManager, mediaDecoder);
        sv= findViewById(R.id.surfaceViewCamera);
        sv.getHolder().addCallback(this);

            }
        });
    }



    private void startCamera(Surface surface) {
        cameraTread.postTask(() -> mediaStreamManager.openStream(surface));
    }


    private void closeCamera (Surface surface){
        cameraTread.postTask(() -> mediaStreamManager.closeStream(surface));
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startCamera(holder.getSurface());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mediaStreamManager.changeSurface(holder.getSurface(), format, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        closeCamera(holder.getSurface());
    }

    // Define OperationResult class
    public static class OperationResult {
        private boolean successful;
        private int streamId;
        private int errorCode;

        public OperationResult(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public int getStreamId() {
            return streamId;
        }

        public void setStreamId(int streamId) {
            this.streamId = streamId;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Zwolnij zasoby Handlera i przerwij cykliczne sprawdzanie poziomu baterii
        if (handler != null && batteryCheckRunnable != null) {
            handler.removeCallbacks(batteryCheckRunnable);
        }
    }
    private void exitApplication() {
        if (projectorEnabled) {
            projectorManager.switchProjector(false);
            projectorEnabled = false;
        }
        finishAffinity();
    }
     s
}
