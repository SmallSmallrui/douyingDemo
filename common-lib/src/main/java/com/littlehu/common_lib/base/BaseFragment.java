package com.littlehu.common_lib.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.littlehu.common_lib.utils.MaterialDialogUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends BaseContract.Presenter> extends RxFragment implements BaseContract.View{
    protected View mRootView;
    private MaterialDialog dialog;
    @Inject
    protected P persenter;

    private Unbinder mUnbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null, false);
        //绑定到butterknife
        mUnbinder = ButterKnife.bind(this, mRootView);
        injectPresenter();
        if(persenter != null){
            persenter.attachView(this);
            persenter.injectLifecycle(this);
            GridLayoutManager manager = new GridLayoutManager(getActivity() ,2);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 0;
                }
            });
        }
        initData();
        initListener();
        return mRootView;
    }

    protected abstract void initListener();

    protected abstract void initData();

    protected abstract void injectPresenter();

    @Override
    public void showDialog() {
        if (this.dialog != null) {
            this.dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog( getContext() , "加载中", true);
            this.dialog = builder.show();
        }
    }

    @Override
    public void dismissDialog() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }


    public abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != Unbinder.EMPTY) mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(persenter != null){
            persenter.detach();
            persenter = null;
        }
        this.mUnbinder = null;
    }
}
