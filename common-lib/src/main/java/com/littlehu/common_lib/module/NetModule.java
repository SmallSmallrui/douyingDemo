package com.littlehu.common_lib.module;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    private static final int TIME_OUT = 10;

    @Singleton
    @Provides
    OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient provideOkhttpClient(OkHttpClient.Builder builder){
        OkHttpClient okHttpClient = builder
                .connectTimeout(TIME_OUT , TimeUnit.SECONDS)
                .readTimeout(TIME_OUT , TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }

    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }


    @Singleton
    @Provides
    Retrofit provideRetrofit(Retrofit.Builder builder , OkHttpClient okHttpClient){
        Retrofit retrofit = builder.baseUrl("http://baobab.kaiyanapp.com/api/").client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create())
                .build();//使用Gson
        return retrofit;
    }


}
