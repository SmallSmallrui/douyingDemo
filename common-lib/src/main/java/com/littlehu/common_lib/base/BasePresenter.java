package com.littlehu.common_lib.base;

import com.trello.rxlifecycle2.LifecycleProvider;

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {
    protected V view;
    protected LifecycleProvider lifecycleProvider;


    @Override
    public void attachView(V view) {
        this.view = view;
    }

    @Override
    public void injectLifecycle(LifecycleProvider lifecycleProvider) {
        this.lifecycleProvider = lifecycleProvider;
    }

    @Override
    public void detach() {
        view = null;
    }
}
