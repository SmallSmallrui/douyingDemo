package com.littlehu.module_video.mvp.contract;

import com.littlehu.common_lib.base.BaseContract;
import com.littlehu.module_video.mvp.model.HomeModel;

public interface VideoContract extends BaseContract {

    interface View extends BaseContract.View{
        void requestSuccess(HomeModel homeModel);

        void fail();
    }

    interface Presenter extends BaseContract.Presenter<VideoContract.View>{
        void requestHomeDatas(int num);
    }

}
