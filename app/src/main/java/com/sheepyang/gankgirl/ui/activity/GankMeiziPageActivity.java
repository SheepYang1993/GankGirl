package com.sheepyang.gankgirl.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.sheepyang.gankgirl.R;
import com.sheepyang.gankgirl.base.RxBaseActivity;
import com.sheepyang.gankgirl.model.gank.GankMeizi;
import com.sheepyang.gankgirl.rx.RxBus;
import com.sheepyang.gankgirl.ui.fragment.MeiziDetailsFragment;
import com.sheepyang.gankgirl.utils.ConstantUtil;
import com.sheepyang.gankgirl.utils.GlideDownloadImageUtil;
import com.sheepyang.gankgirl.utils.ImmersiveUtil;
import com.sheepyang.gankgirl.utils.LogUtil;
import com.sheepyang.gankgirl.utils.ShareUtil;
import com.sheepyang.gankgirl.widget.DepthTransFormes;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GankMeiziPageActivity extends RxBaseActivity
{

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    private static final String EXTRA_INDEX = "extra_index";

    private int currenIndex;

    private String url;

    private Realm realm;

    private boolean isHide = false;

    private RealmResults<GankMeizi> gankMeizis;


    @Override
    public int getLayoutId()
    {

        return R.layout.activity_meizi_pager;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
        {
            currenIndex = intent.getIntExtra(EXTRA_INDEX, -1);
        }

        realm = Realm.getDefaultInstance();
        gankMeizis = realm.where(GankMeizi.class).findAll();

        mViewPager.setAdapter(new MeiziPagerAdapter(getFragmentManager()));
        mViewPager.setPageTransformer(true, new DepthTransFormes());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                mToolbar.setTitle(gankMeizis.get(position).getDesc());
                currenIndex = position;
                url = gankMeizis.get(currenIndex).getUrl();

                //切换ViewPager时隐藏ToolBar
                ImmersiveUtil.enter(GankMeiziPageActivity.this);
                mAppBarLayout.animate()
                        .translationY(-mAppBarLayout.getHeight())
                        .setInterpolator(new DecelerateInterpolator(2))
                        .start();
                isHide = true;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        RxBus.getInstance().toObserverable(String.class)
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {

                        LogUtil.all("隐藏toolbar");
                        hideOrShowToolbar();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        LogUtil.all("接收失败");
                    }
                });
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle(gankMeizis.get(currenIndex).getDesc());
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                onBackPressed();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_meizi);


        //设置toolbar的颜色
        mAppBarLayout.setAlpha(0.5f);
        mToolbar.setBackgroundResource(R.color.black_90);
        mAppBarLayout.setBackgroundResource(R.color.black_90);

        //menu的点击事件
        saveImage();
        shareImage();
    }


    @Override
    protected void onResume()
    {

        super.onResume();
        mViewPager.setCurrentItem(currenIndex);
        url = gankMeizis.get(currenIndex).getUrl();
    }

    public static void luanch(Activity activity, int index)
    {

        Intent mIntent = new Intent(activity, GankMeiziPageActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(EXTRA_INDEX, index);
        activity.startActivity(mIntent);
    }


    /**
     * 保存图片到本地
     */
    private void saveImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_save))
                .compose(bindToLifecycle())
                .compose(RxPermissions.getInstance(GankMeiziPageActivity.this).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(new Func1<Boolean,Boolean>()
                {

                    @Override
                    public Boolean call(Boolean aBoolean)
                    {

                        return aBoolean;
                    }
                })
                .flatMap(new Func1<Boolean,Observable<Uri>>()
                {

                    @Override
                    public Observable<Uri> call(Boolean aBoolean)
                    {

                        return GlideDownloadImageUtil.saveImageToLocal(GankMeiziPageActivity.this, url);
                    }
                })
                .map(new Func1<Uri,String>()
                {

                    @Override
                    public String call(Uri uri)
                    {

                        String msg = String.format("图片已保存至 %s 文件夹",
                                new File(Environment.getExternalStorageDirectory(), ConstantUtil.FILE_DIR)
                                        .getAbsolutePath());
                        return msg;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {

                        Toast.makeText(GankMeiziPageActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(GankMeiziPageActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 分享图片
     */
    public void shareImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_share))
                .compose(bindToLifecycle())
                .compose(RxPermissions.getInstance(GankMeiziPageActivity.this).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(new Func1<Boolean,Boolean>()
                {

                    @Override
                    public Boolean call(Boolean aBoolean)
                    {

                        return aBoolean;
                    }
                })
                .flatMap(new Func1<Boolean,Observable<Uri>>()
                {

                    @Override
                    public Observable<Uri> call(Boolean aBoolean)
                    {

                        return GlideDownloadImageUtil.saveImageToLocal(GankMeiziPageActivity.this, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Action1<Uri>()
                {

                    @Override
                    public void call(Uri uri)
                    {

                        ShareUtil.sharePic(uri, gankMeizis.get(currenIndex).getDesc(), GankMeiziPageActivity.this);
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(GankMeiziPageActivity.this, "分享失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void hideOrShowToolbar()
    {

        if (isHide)
        {
            //显示
            ImmersiveUtil.exit(this);
            mAppBarLayout.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = false;
        } else
        {
            //隐藏
            ImmersiveUtil.enter(this);
            mAppBarLayout.animate()
                    .translationY(-mAppBarLayout.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = true;
        }
    }


    private class MeiziPagerAdapter extends FragmentStatePagerAdapter
    {

        public MeiziPagerAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            return MeiziDetailsFragment.newInstance(gankMeizis.get(position).getUrl());
        }


        @Override
        public int getCount()
        {

            return gankMeizis.size();
        }
    }
}
