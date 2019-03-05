package com.littlehu.module_video.mvp.ui.adapter;


import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.littlehu.module_video.R;
import com.littlehu.module_video.mvp.model.HomeModel;
import com.littlehu.module_video.mvp.ui.view.ILHVideoPlayer;

import java.util.List;

public class VideoRecyclerAdapter extends BaseQuickAdapter<HomeModel.IssueListBean.ItemListBean.DataBean , BaseViewHolder> {


    public VideoRecyclerAdapter(int layoutResId, @Nullable List<HomeModel.IssueListBean.ItemListBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeModel.IssueListBean.ItemListBean.DataBean item) {
        ILHVideoPlayer player = helper.getView(R.id.lh_video);
        player.setDataSource(item.getPlayUrl() , null );
        if(helper.getPosition() == 0){
            player.start();
        }
        helper.setText(R.id.tv_title , item.getTitle())
                .setText(R.id.tv_author , item.getDescription());

    }
}
