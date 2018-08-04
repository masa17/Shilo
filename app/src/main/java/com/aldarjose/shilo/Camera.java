package com.aldarjose.shilo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

//////ADD FLASH BUTTON
//////ADD VIDEO

public class Camera extends AppCompatActivity {

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_camera);

        setUpCameraView();

    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    private void setUpCameraView() {
        mCameraView = findViewById(R.id.camera);
        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Log.d("testing-tag", String.valueOf(cameraKitImage.getJpeg()));
                new SavePhotoTask(Camera.this).execute(cameraKitImage.getJpeg());

                Intent myIntent = new Intent(Camera.this, Post.class);
                startActivity(myIntent);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
                /////////Figure out how to add 9 sec videos;
            }
        });

        Button cameraButton = findViewById(R.id.buttonCamera); ///////////Replace with an ImageButton
        cameraButton.bringToFront();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.captureImage();
            }
        });
    }
}
