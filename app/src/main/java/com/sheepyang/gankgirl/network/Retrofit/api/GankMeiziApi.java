package com.sheepyang.gankgirl.network.Retrofit.api;


import com.sheepyang.gankgirl.model.meizi.gank.GankMeiziResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GankMeiziApi
{

    @GET("data/福利/{number}/{page}")
    Observable<GankMeiziResult> getGankMeizi(@Path("number") int number, @Path("page") int page);
}
