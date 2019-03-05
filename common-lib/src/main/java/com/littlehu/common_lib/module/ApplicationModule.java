package com.littlehu.common_lib.module;

import android.app.Application;

import com.littlehu.common_lib.TestApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private TestApplication mApplication;

    public ApplicationModule(TestApplication application){
        this.mApplication = application;
    }

    @Singleton
    @Provides
    public TestApplication provideApplication(){
        return mApplication;
    }




}
