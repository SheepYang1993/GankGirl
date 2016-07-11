package com.sheepyang.gankgirl;

import android.app.Application;
import android.content.Context;

import com.sheepyang.gankgirl.utils.ConstantUtil;
import com.tencent.bugly.crashreport.CrashReport;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by hcc on 16/6/25 12:07
 * 100332338@qq.com
 * <p/>
 * 萌妹子App
 */
public class GankGirlApp extends Application
{

    public static Context mAppContext;

    @Override
    public void onCreate()
    {

        super.onCreate();
        mAppContext = this;
        // 配置Realm数据库
        RealmConfiguration configuration = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().schemaVersion(4).migration(new RealmMigration()
        {

            @Override
            public void migrate(DynamicRealm realm, long oldVersion, long newVersion)
            {

            }
        }).build();

        Realm.setDefaultConfiguration(configuration);

        //配置腾讯bugly
        CrashReport.initCrashReport(getApplicationContext(), ConstantUtil.BUGLY_ID, false);
    }


    public static Context getContext()
    {

        return mAppContext;
    }
}
