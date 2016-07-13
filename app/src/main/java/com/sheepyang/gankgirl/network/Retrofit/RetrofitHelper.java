package com.sheepyang.gankgirl.network.Retrofit;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.sheepyang.gankgirl.network.Retrofit.api.DoubanMeizhiApi;
import com.sheepyang.gankgirl.network.Retrofit.api.GankMeiziApi;
import com.sheepyang.gankgirl.network.Retrofit.api.HuaBanMeiziApi;
import com.sheepyang.gankgirl.network.Retrofit.api.JianDanMeiziApi;
import com.sheepyang.gankgirl.network.Retrofit.api.SchoolFriendsApi;
import com.sheepyang.gankgirl.network.Retrofit.api.TaoFemaleaApi;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitHelper
{


    public static final String BASE_GANK_URL = "http://gank.io/api/";

    public static final String BASE_HUABAN_URL = "http://route.showapi.com/";

    public static final String BASE_DOUBAN_URL = "http://www.dbmeinv.com/dbgroup/";

    public static final String BASE_JIANDAN_URL = "http://jandan.net/";

    private static OkHttpClient mOkHttpClient;

    static
    {
        initOkHttpClient();
    }

    /**
     * Gank妹子Api
     *
     * @return
     */
    public static GankMeiziApi getGankMeiziApi()
    {

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_GANK_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GankMeiziApi gankMeiziApi = mRetrofit.create(GankMeiziApi.class);

        return gankMeiziApi;
    }


    /**
     * 花瓣Api
     *
     * @return
     */
    public static HuaBanMeiziApi getHuaBanMeiziApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HUABAN_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        HuaBanMeiziApi huaBanMeiziApi = retrofit.create(HuaBanMeiziApi.class);

        return huaBanMeiziApi;
    }

    /**
     * 豆瓣Api
     *
     * @return
     */
    public static DoubanMeizhiApi getDoubanMeiziApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_DOUBAN_URL)
                .client(new OkHttpClient())
                .build();

        return retrofit.create(DoubanMeizhiApi.class);
    }

    /**
     * 校友圈Api
     *
     * @return
     */
    public static SchoolFriendsApi getSchoolFriendsApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HUABAN_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(SchoolFriendsApi.class);
    }

    /**
     * 淘女郎Api
     *
     * @return
     */
    public static TaoFemaleaApi getTaoFemaleApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HUABAN_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TaoFemaleaApi.class);
    }

    /**
     * 煎蛋Api
     *
     * @return
     */
    public static JianDanMeiziApi getJianDanApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_JIANDAN_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(JianDanMeiziApi.class);
    }


    /**
     * 初始化OKHttpClient
     */
    private static void initOkHttpClient()
    {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null)
        {
            synchronized (RetrofitHelper.class)
            {
                if (mOkHttpClient == null)
                {
                    //设置Http缓存
                    Cache cache = new Cache(new File(com.sheepyang.gankgirl.GankGirlApp.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }
}
