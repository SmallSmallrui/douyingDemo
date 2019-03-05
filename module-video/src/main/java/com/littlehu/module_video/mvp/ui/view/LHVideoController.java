package com.littlehu.module_video.mvp.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.littlehu.common_lib.utils.DensityUtil;
import com.littlehu.module_video.R;

public class LHVideoController extends BaseLHVideoPlayerController {

    private ImageView pauseView;


    public LHVideoController(@NonNull Context context) {
        this(context , null);
    }

    public LHVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setImage(int resId) {

    }

    @Override
    public ImageView imageView() {
        return null;
    }

    @Override
    public void setLength(long length) {

    }

    @Override
    protected void onPlayStateChanged(int playState) {

    }

    @Override
    protected void onPlayModeChanged(int playMode) {

    }

    @Override
    protected void reset() {

    }

    @Override
    protected void updateProgress() {

    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {

    }

    @Override
    protected void hideChangePosition() {

    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {

    }

    @Override
    protected void hideChangeVolume() {

    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {

    }

    @Override
    protected void hideChangeBrightness() {

    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_video_controller , this , true);

    }


}
