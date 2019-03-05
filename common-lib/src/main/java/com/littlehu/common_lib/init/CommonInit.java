package com.littlehu.common_lib.init;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.littlehu.common_lib.BuildConfig;
import com.littlehu.common_lib.ILoader;


public class CommonInit implements ILoader {

    @Override
    public void loader(Application application) {

        //初始化阿里路由框架
        if (BuildConfig.DEBUG) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
        //Utils.init(application);
    }


}
