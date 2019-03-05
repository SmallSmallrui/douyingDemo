package com.littlehu.module_video.mvp.ui.view;

/**
 * Created by Thinkpad on 2019/2/28.
 */

/**
 * 视频播放管理器
 */
public class LHVideoPlayerManager {




    private LHVideoPlayer mLhVideoPlayer;


    private LHVideoPlayerManager(){

    }

    private static LHVideoPlayerManager sIntance;


    public static LHVideoPlayerManager getInstance(){
        if(sIntance == null){
            synchronized (LHVideoPlayerManager.class) {
                if(sIntance == null) {
                    sIntance = new LHVideoPlayerManager();
                }
            }
        }
        return sIntance;
    }


    public LHVideoPlayer getCurrentLHVideoPlayer() {
        return mLhVideoPlayer;
    }

    public void setCurrentLHVideoPlayer(LHVideoPlayer player){
        //切换播放
        if(mLhVideoPlayer != player){
            releaseLHVideoPlayer();
            mLhVideoPlayer = player;
        }
    }

    public void releaseLHVideoPlayer() {
        if(mLhVideoPlayer != null){
            mLhVideoPlayer.release();
            mLhVideoPlayer = null;
        }
    }

    public void pauseLHVideoPlayer(){
        if(mLhVideoPlayer != null && (mLhVideoPlayer.isPlaying() || mLhVideoPlayer.isBufferingPlaying())){
            mLhVideoPlayer.pause();
        }
    }

    public void restartLHVideoPlayer(){
        if(mLhVideoPlayer != null && (mLhVideoPlayer.isPaused() || mLhVideoPlayer.isBufferingPaused())){
            mLhVideoPlayer.restart();
        }
    }

    public boolean onBackPressd() {
        if (mLhVideoPlayer != null) {
           /* if (mLhVideoPlayer.isFullScreen()) {
                return mLhVideoPlayer.exitFullScreen();
            } else if (mLhVideoPlayer.isTinyWindow()) {
                return mLhVideoPlayer.exitTinyWindow();
            }*/
        }
        return false;
    }

}
