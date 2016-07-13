package com.sheepyang.gankgirl.network.Retrofit.api;

import com.sheepyang.gankgirl.model.school.schoolmodel.SchoolFriends;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hcc on 16/6/25 19:44
 * 100332338@qq.com
 */
public interface SchoolFriendsApi
{

    @GET("126-2")
    Observable<SchoolFriends> getSchoolFriends(@Query("page") String page,
                                           @Query("showapi_appid") String appId,
                                           @Query("showapi_sign") String sign);
}
