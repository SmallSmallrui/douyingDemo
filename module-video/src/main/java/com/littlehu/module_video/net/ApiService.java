package com.littlehu.module_video.net;

import com.littlehu.module_video.mvp.model.HomeModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    @GET("http://baobab.kaiyanapp.com/api/v2/feed")
    Observable<HomeModel> getFirstHomeData(@Query("num") int num);

}
