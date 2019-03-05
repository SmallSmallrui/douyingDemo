package com.littlehu.module_video.mvp.presenter;

import com.littlehu.common_lib.base.BasePresenter;
import com.littlehu.module_video.mvp.contract.VideoContract;
import com.littlehu.module_video.mvp.model.HomeModel;
import com.littlehu.module_video.net.ApiService;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VideoPresenter extends BasePresenter<VideoContract.View> implements VideoContract.Presenter{
    ApiService apiService;

    @Inject
    public VideoPresenter(ApiService apiService){
        this.apiService = apiService;
    }

    @Override
    public void requestHomeDatas(int num) {
        apiService.getFirstHomeData(num)
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HomeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        view.showDialog();
                    }

                    @Override
                    public void onNext(HomeModel homeModel) {
                        view.requestSuccess(homeModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        view.dismissDialog();
                    }
                });
    }
}
