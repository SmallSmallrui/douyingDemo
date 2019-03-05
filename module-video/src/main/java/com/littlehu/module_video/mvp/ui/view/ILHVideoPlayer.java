package com.littlehu.module_video.mvp.ui.view;

/**
 * Created by Thinkpad on 2019/2/28.
 */

import java.util.Map;

/**
 * LHVideoPlayer的抽象接口
 */
public interface ILHVideoPlayer {

    /**
     *  设置视频的 url ，以及headers
     * @param url
     * @param header
     */
    void setDataSource(String url, Map<String, String> header);


    /**
     *  开始播放
     */
    void start();

    /**
     * 重新播放，播放器被暂停、播放错误、播放完成后，需要调用此方法重新播放
     */
    void restart();


    /**
     * 暂停播放
     */
    void pause();


    /**
     * seek 到指定的位置 播放
     * @param position
     */
    void seekTo(long position);


    /**
     * 设置音量
     *
     * @param volume 音量值
     */
    void setVolume(int volume);



    /**
     * 开始播放时，是否从上一次的位置继续播放
     *
     * @param continueFromLastPosition true 接着上次的位置继续播放，false从头开始播放
     */
    void continueFromLastPosition(boolean continueFromLastPosition);


    void releasePlayer();


    void release();



    /*********************************
     * 以下9个方法是播放器在当前的播放状态
     **********************************/
    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();


    /*********************************
     * 以下3个方法是播放器的模式
     **********************************/
    boolean isFullScreen();
    boolean isTinyWindow();
    boolean isNormal();


    /**
     * 获取最大音量
     *
     * @return 最大音量值
     */
    int getMaxVolume();

    /**
     * 获取当前音量
     *
     * @return 当前音量值
     */
    int getVolume();

    /**
     * 获取办法给总时长，毫秒
     *
     * @return 视频总时长ms
     */
    long getDuration();


    /**
     * 获取当前播放的位置，毫秒
     *
     * @return 当前播放位置，ms
     */
    long getCurrentPosition();

    /**
     * 获取视频缓冲百分比
     *
     * @return 缓冲白百分比
     */
    int getBufferPercentage();

}
