package com.littlehu.common_lib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.littlehu.common_lib.utils.MaterialDialogUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends BasePresenter> extends RxAppCompatActivity implements BaseContract.View {
    private Unbinder unbinder;
    private MaterialDialog dialog;

    @Inject
    protected P persenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        injectPresenter();
        if (persenter != null) {
            persenter.attachView(this);
            persenter.injectLifecycle(this);
        }

    }

    protected abstract void injectPresenter();


    public abstract int getLayoutId();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (persenter != null) {
            persenter.detach();
        }

    }


    @Override
    public void showDialog() {
        if (this.dialog != null) {
            this.dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, "加载中", true);
            this.dialog = builder.show();
        }

    }


    @Override
    public void dismissDialog() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }
}
