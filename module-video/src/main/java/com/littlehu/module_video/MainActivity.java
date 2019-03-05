package com.littlehu.module_video;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.littlehu.module_video.mvp.ui.fragment.VideoFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoFragment fragment = VideoFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.container , fragment).commitAllowingStateLoss();

    }
}
