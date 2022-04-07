package com.marathon.androidimagepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        VideoView view = (VideoView) findViewById(R.id.videoView);
        String uriPath = "android:resource://" + getPackageName() + "/" + R.raw.test;
        Uri uri2 = Uri.parse(uriPath);
        view.setVideoURI(uri2);
        view.requestFocus();
        view.start();
    }
}