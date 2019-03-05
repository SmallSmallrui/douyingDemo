package com.littlehu.module_video.mvp.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Thinkpad on 2019/3/4.
 */

public abstract class BaseLHVideoPlayerController extends FrameLayout implements View.OnTouchListener{
    protected Context mContext;

    protected ILHVideoPlayer mLhVideoPlayer;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private static final int THRESHOLD = 80;

    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition; //是否需要改变进度
    private boolean mNeedChangeVolume;  //是否需要改变声音
    private boolean mNeedChangeBrightness; //是否需要改变亮度

    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

    public BaseLHVideoPlayerController(@NonNull Context context) {
        this(context , null);
    }

    public BaseLHVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setOnTouchListener(this);

    }

    public void setLHVideoPlayer(ILHVideoPlayer lhVideoPlayer){
        this.mLhVideoPlayer = lhVideoPlayer;
    }

    /**
     * 设置视频的标题
     * @param title
     */
    public abstract void setTitle(String title);


    /**
     * 视频底图
     * @param resId
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片加载工具来加载网络图片
     * @return
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长
     * @param length
     */
    public abstract void setLength(long length);

    /**
     * 当播放器的播放状态发生变化，在此方法中你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     *                  <li>{@link LHVideoPlayer#STATE_IDLE}</li>
     *                  <li>{@link LHVideoPlayer#STATE_PREPARING}</li>
     *                  <li>{@link LHVideoPlayer#STATE_PREPARED}</li>
     *                  <li>{@link LHVideoPlayer#STATE_PLAYING}</li>
     *                  <li>{@link LHVideoPlayer#STATE_PAUSED}</li>
     *                  <li>{@link LHVideoPlayer#STATE_BUFFERING_PLAYING}</li>
     *                  <li>{@link LHVideoPlayer#STATE_BUFFERING_PAUSED}</li>
     *                  <li>{@link LHVideoPlayer#STATE_ERROR}</li>
     *                  <li>{@link LHVideoPlayer#STATE_COMPLETED}</li>
     *                  </ul>
     */
    protected abstract void onPlayStateChanged(int playState);


    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 <ul>
     *                 <li>{@link LHVideoPlayer#MODE_NORMAL}</li>
     *                 <li>{@link LHVideoPlayer#MODE_FULL_SCREEN}</li>
     *                 <li>{@link LHVideoPlayer#MODE_TINY_WINDOW}</li>
     *                 </ul>
     */
    protected abstract void onPlayModeChanged(int playMode);


    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();


    protected void startUpdateProgressTimer(){
        cancelUpdateProgressTimer();

        if(mUpdateProgressTimer == null){
            mUpdateProgressTimer = new Timer();
        }

        if(mUpdateProgressTimerTask == null){
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    BaseLHVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }

        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask , 0 ,1000);

    }


    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer(){
        if ( mUpdateProgressTimer != null ){
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }

        if(mUpdateProgressTimerTask != null){
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }

    }


    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();


    //用来分析 用户行为 ，（加减亮度，加减音量，控制视频播放进度）
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //首先判断是否是全屏状态，如果不是全屏状态则无法调整 播放进度，亮度，声音
        if(!mLhVideoPlayer.isFullScreen()) {
            return false;
        }

        //判断是否 处在播放中，播放缓冲，暂停的时候能够拖动改变位置，亮度，声音
        if(mLhVideoPlayer.isIdle()
                || mLhVideoPlayer.isError()
                || mLhVideoPlayer.isPrepared()
                || mLhVideoPlayer.isPrepared()
                || mLhVideoPlayer.isCompleted()){
            hideChangePosition();
            hideChangeBrightness();
            hideChangeVolume();
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if(!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness ){

                    if(absDeltaX >= THRESHOLD){
                        //如果在横坐标上位移超过则 调整进度
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = mLhVideoPlayer.getCurrentPosition();
                    } else if (absDeltaY >= THRESHOLD){
                        //如果在纵坐标上位移超过默认值

                        if( mDownX < getWidth() * 0.5 ){
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = ((Activity)mContext).getWindow().getAttributes().screenBrightness;

                        }else {
                            // 右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mLhVideoPlayer.getVolume();

                        }

                    }
                }

                //如果需要改进度则执行
                if(mNeedChangePosition){
                    long duration = mLhVideoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0 , Math.min(duration , toPosition));
                    int newPositionProgress = (int)(100f * mNewPosition / duration);
                    showChangePosition(duration , newPositionProgress);
                }

                //是否需要改变亮度
                if(mNeedChangeBrightness){
                    deltaY = -deltaY;
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0 , Math.min(newBrightness , 1));

                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = ((Activity)mContext).getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    ((Activity)mContext).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }

                //是否改变音量
                if(mNeedChangeVolume){
                    deltaY = -deltaY;
                    int maxVolume = mLhVideoPlayer.getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0 ,Math.min(maxVolume , newVolume));
                    mLhVideoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mNeedChangePosition){
                    mLhVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if(mNeedChangeBrightness){
                    hideChangeBrightness();
                    return true;
                }
                if(mNeedChangeVolume){
                    hideChangeVolume();
                    return true;
                }
                break;
        }

        return false;
    }


    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    /**
     * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();

}
