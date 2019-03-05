package com.littlehu.common_lib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.littlehu.common_lib.config.ModuleLifecyleConfig;
import com.littlehu.common_lib.di.ApplicationComponent;
import com.littlehu.common_lib.di.DaggerApplicationComponent;
import com.littlehu.common_lib.module.ApplicationModule;
import com.littlehu.common_lib.module.NetModule;
import com.littlehu.common_lib.utils.AppManager;

import dagger.Component;

public class TestApplication extends Application {

    ApplicationComponent applicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        ModuleLifecyleConfig.getInstance().initModule(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getAppManager().removeActivity(activity);
            }
        });
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this))
                .netModule(new NetModule()).build();
    }

    public ApplicationComponent getApplicationComponent() {
         return applicationComponent;
    }
}
