package com.littlehu.module_video.di.module;

import com.littlehu.common_lib.scope.ActivityScope;
import com.littlehu.module_video.net.ApiService;

import javax.inject.Scope;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    @Provides
    public ApiService provideApiservice(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }


}
