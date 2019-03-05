package com.littlehu.common_lib.di;

import com.littlehu.common_lib.TestApplication;
import com.littlehu.common_lib.module.ApplicationModule;
import com.littlehu.common_lib.module.NetModule;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {ApplicationModule.class , NetModule.class})
public interface ApplicationComponent {

    TestApplication Application();

    Retrofit retrofit();


}
