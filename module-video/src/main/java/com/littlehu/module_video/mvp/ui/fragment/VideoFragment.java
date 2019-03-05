package com.littlehu.module_video.mvp.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.littlehu.common_lib.TestApplication;
import com.littlehu.common_lib.base.BaseFragment;
import com.littlehu.common_lib.utils.ToastUtils;
import com.littlehu.module_video.R;
import com.littlehu.module_video.di.component.DaggerVideoComponent;
import com.littlehu.module_video.mvp.contract.VideoContract;
import com.littlehu.module_video.mvp.model.HomeModel;
import com.littlehu.module_video.mvp.presenter.VideoPresenter;
import com.littlehu.module_video.mvp.ui.adapter.VideoRecyclerAdapter;
import com.littlehu.module_video.mvp.ui.view.LHVideoPlayer;
import com.littlehu.module_video.mvp.ui.view.LHVideoPlayerManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoFragment extends BaseFragment<VideoPresenter> implements VideoContract.View ,BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int pageNum = 1 ;

    List<HomeModel.IssueListBean.ItemListBean.DataBean> list = new ArrayList<>();
    VideoRecyclerAdapter adapter;
    private int firstVisibleItem;
    private int lastVisibleItem;
    private int oldPosition;
    private int newPosition;

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 0;
                persenter.requestHomeDatas(pageNum);
                adapter.setEnableLoadMore(false);
            }
        });
    }

    @Override
    protected void initData() {
        persenter.requestHomeDatas(pageNum);
    }

    @Override
    protected void injectPresenter() {
        DaggerVideoComponent.builder()
                .applicationComponent(((TestApplication) (getActivity().getApplication())).getApplicationComponent())
                .build()
                .inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        adapter = new VideoRecyclerAdapter(R.layout.item_video, list);
        adapter.setOnLoadMoreListener(this , recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        /**在这里执行，视频的自动播放与停止*/
                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        oldPosition = newPosition;
                        newPosition = snapHelper.findTargetSnapPosition(manager, 0, 0);
                        if(oldPosition == newPosition){
                            return;
                        }
                        Toast.makeText(getContext(), "滑动到了" + newPosition + "的位置", Toast.LENGTH_SHORT).show();

                        View view = snapHelper.findSnapView(manager);
                        LHVideoPlayerManager.getInstance().releaseLHVideoPlayer();
                        RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);

                        if (viewHolder != null && viewHolder instanceof BaseViewHolder) {
                            LHVideoPlayer player = ((BaseViewHolder) viewHolder).getView(R.id.lh_video);
                            player.start();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //int lastVisibleItem = ((LinearLayoutManager)(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                firstVisibleItem = ((LinearLayoutManager) (recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                lastVisibleItem = ((LinearLayoutManager) (recyclerView.getLayoutManager())).findLastVisibleItemPosition();


            }
        });
    }

    @Override
    public void requestSuccess(HomeModel homeModel) {
        if(pageNum == 1){
            list.clear();
        }
        adapter.setEnableLoadMore(true);
        mSwipeRefreshLayout.setRefreshing(false);
        HomeModel.IssueListBean datas = homeModel.getIssueList().get(0);
        for (int i = 0; i < datas.getItemList().size(); i++) {
            if (datas.getItemList().get(i).getType().equals("video")) {
                list.add(datas.getItemList().get(i).getData());
            }
        }
        pageNum += 1;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void fail() {
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LHVideoPlayerManager.getInstance().releaseLHVideoPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        LHVideoPlayerManager.getInstance().pauseLHVideoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        LHVideoPlayerManager.getInstance().restartLHVideoPlayer();
    }

    @Override
    public void onLoadMoreRequested() {
        persenter.requestHomeDatas(pageNum);
    }


    /*private void autoPlayVideo(RecyclerView recyclerView) {

        if (firstVisibleItem == 0 && lastVisibleItem == 0 && recyclerView.getChildAt(0) != null) {

            LHVideoPlayer videoView = null;
            if (recyclerView != null && recyclerView.getChildAt(0) != null) {
                videoView = recyclerView.getChildAt(0).findViewById(R.id.lh_video);
            }
            if (videoView != null) {
                if (videoView.isIdle()||videoView.isPaused()) {
                    videoView.start();
                }
            }
        }

        for (int i = 0; i <= lastVisibleItem; i++) {
            if (recyclerView == null || recyclerView.getChildAt(i) == null) {
                return;
            }
            LHVideoPlayer
                    videoView = recyclerView.getChildAt(i).findViewById(R.id.lh_video);
            if (videoView != null) {

                Rect rect = new Rect();
                //获取视图本身的可见坐标，把值传入到rect对象中
                videoView.getLocalVisibleRect(rect);
                //获取视频的高度
                int videoHeight = videoView.getHeight();

                if (rect.top <= 100 && rect.bottom >= videoHeight) {
                    if (videoView.isIdle() || videoView.isPaused()) {
                        videoView.start();
                    }
                    return;
                }

                LHVideoPlayerManager.getInstance().releaseLHVideoPlayer();

            } else {
                LHVideoPlayerManager.getInstance().releaseLHVideoPlayer();
            }

        }

    }*/
}
