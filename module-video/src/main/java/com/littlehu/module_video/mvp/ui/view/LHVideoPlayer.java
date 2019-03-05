package com.littlehu.module_video.mvp.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.littlehu.common_lib.utils.DensityUtil;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * Created by Thinkpad on 2019/2/28.
 */

public class LHVideoPlayer extends FrameLayout implements ILHVideoPlayer , TextureView.SurfaceTextureListener{
    private Context mContext;

    //mContainer为背景
    private FrameLayout mContainer;

    //播放地址
    private String mUrl;
    //请求头部
    private Map<String, String> mHeader;

    /**
     * 音量管理
     */
    private AudioManager mAudioManager;

    /**
     * 视频播放者
     */
    private IMediaPlayer mMediaPlayer;




    private LHTextureView mLhTextureView;

    private SurfaceTexture mSurfaceTexture;

    private Surface mSurface;

    private BaseLHVideoPlayerController mController;

    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 7;

    /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 12;

    private int mBufferPercentage;

    /**
     * 当前状态
     */
    private int mCurrentState = STATE_IDLE;

    private int mCurrentMode = MODE_NORMAL;

    public LHVideoPlayer(@NonNull Context context) {
        this(context , null);
    }

    public LHVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public LHVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    //初始化
    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer , params);
    }

    @Override
    public void setDataSource(String url, Map<String, String> header) {
        this.mUrl = url;
        this.mHeader = header;
    }

    @Override
    public void start() {
        if(mCurrentState == STATE_IDLE){
            LHVideoPlayerManager.getInstance().setCurrentLHVideoPlayer(this);
            //初始化音量管理
            initAudioManager();
            //初始化 mediaPlayer();
            initMediaPlayer();
            //初始化 视频播放者
            initTextureView();
            //添加视频播放者
            addTextureView();
        }
    }




    private void initAudioManager() {
        if(mAudioManager == null){
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus( null , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN);
        }
    }


    private void initMediaPlayer() {
        if(mMediaPlayer == null){
            mMediaPlayer = new AndroidMediaPlayer();
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    private void initTextureView() {
        if(mLhTextureView == null){
            mLhTextureView = new LHTextureView(mContext);
            mLhTextureView.setSurfaceTextureListener(this);
            mLhTextureView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }


    private void addTextureView() {
        mContainer.removeView(mLhTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(mLhTextureView, 0, params);
    }


    public void addController(BaseLHVideoPlayerController controller){
        mContainer.removeView(controller);
        mController = controller;
        mController.reset();
        mController.setLHVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(mController , params);

    }


    @Override
    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            changeControllerState();
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            changeControllerState();
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
            openMediaPlayer();
        } else {

        }
    }

    private void changeControllerState(){
        if(mController != null){
            mController.onPlayStateChanged(mCurrentState);
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            changeControllerState();
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            changeControllerState();
        }
    }

    @Override
    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {
        //this.continueFromLastPosition = continueFromLastPosition;
    }

    @Override
    public void releasePlayer() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mLhTextureView);
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentState = STATE_IDLE;
    }

    @Override
    public void release() {
        // 保存播放位置
        /*if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            NiceUtil.savePlayPosition(mContext, mUrl, getCurrentPosition());
        } else if (isCompleted()) {
            NiceUtil.savePlayPosition(mContext, mUrl, 0);
        }*/
        // 退出全屏或小窗口
        /*if (isFullScreen()) {
            exitFullScreen();
        }
        if (isTinyWindow()) {
            exitTinyWindow();
        }*/
        mCurrentMode = MODE_NORMAL;

        // 释放播放器
        releasePlayer();

        // 恢复控制器
        if (mController != null) {
            mController.reset();
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return mCurrentMode == MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mCurrentMode == MODE_NORMAL;
    }


    @Override
    public int getMaxVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if(mSurfaceTexture == null){
            mSurfaceTexture = surface;
            openMediaPlayer();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mLhTextureView.setSurfaceTexture(mSurfaceTexture);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private void openMediaPlayer(){
        mContainer.setKeepScreenOn(true);

        mMediaPlayer.setOnPreparedListener(onPreparedListener);

        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);

        mMediaPlayer.setOnCompletionListener(onCompletionListener);

        mMediaPlayer.setOnErrorListener(onErrorListener);

        mMediaPlayer.setOnInfoListener(onInfoListener);

        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);


        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeader);
            if(mSurface == null){
                mSurface = new Surface(mSurfaceTexture);
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.prepareAsync();
                mCurrentState = STATE_PREPARING;
                changeControllerState();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_PREPARED;
            changeControllerState();
            iMediaPlayer.start();
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener
            = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            mLhTextureView.adaptVideoSize(width, height);
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mCurrentState = STATE_COMPLETED;
            changeControllerState();
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mCurrentState = STATE_ERROR;
                changeControllerState();
            }
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            if(what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                // 播放器开始渲染
                mCurrentState = STATE_PLAYING;
                changeControllerState();
            }else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START){
                if(mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED){
                    mCurrentState = STATE_BUFFERING_PAUSED;
                }else{
                    mCurrentState = STATE_BUFFERING_PLAYING;
                }
                changeControllerState();
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    changeControllerState();
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    changeControllerState();
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (mLhTextureView != null) {
                    mLhTextureView.setRotation(extra);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {

            } else {

            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener
            = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };


}
