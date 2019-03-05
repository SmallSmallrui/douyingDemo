package com.littlehu.common_lib.base;

import com.trello.rxlifecycle2.LifecycleProvider;

public interface BaseContract {

    interface View{
        void showDialog();

        void dismissDialog();
    }

    interface Presenter< T extends View>{
        void attachView(T view);

        void injectLifecycle(LifecycleProvider lifecycleProvider);

        void detach();
    }

}
