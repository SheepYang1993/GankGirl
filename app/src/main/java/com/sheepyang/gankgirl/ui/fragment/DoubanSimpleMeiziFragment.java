package com.sheepyang.gankgirl.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewTreeObserver;

import com.sheepyang.gankgirl.R;
import com.sheepyang.gankgirl.adapter.DoubanMeiziAdapter;
import com.sheepyang.gankgirl.adapter.base.AbsRecyclerViewAdapter;
import com.sheepyang.gankgirl.base.RxBaseFragment;
import com.sheepyang.gankgirl.model.douban.DoubanMeizi;
import com.sheepyang.gankgirl.network.RetrofitHelper;
import com.sheepyang.gankgirl.ui.activity.DoubanMeiziPageActivity;
import com.sheepyang.gankgirl.utils.MeiziCacheUtil;
import com.sheepyang.gankgirl.utils.SnackbarUtil;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DoubanSimpleMeiziFragment extends RxBaseFragment
{


    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PRELOAD_SIZE = 6;

    public static final String EXTRA_CID = "extra_cid";

    public static final String EXTRA_TYPE = "extra_type";

    private boolean mIsLoadMore = true;

    private int cid;

    private int pageNum = 20;

    private int page = 1;

    private DoubanMeiziAdapter mAdapter;

    private StaggeredGridLayoutManager mLayoutManager;

    private int type;

    private Realm realm;

    private RealmResults<DoubanMeizi> doubanMeizis;


    public static DoubanSimpleMeiziFragment newInstance(int cid, int type)
    {

        DoubanSimpleMeiziFragment mDoubanSimpleMeiziFragment = new DoubanSimpleMeiziFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CID, cid);
        bundle.putInt(EXTRA_TYPE, type);
        mDoubanSimpleMeiziFragment.setArguments(bundle);

        return mDoubanSimpleMeiziFragment;
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_simple_douban_meizi;
    }

    @Override
    public void initViews()
    {

        realm = Realm.getDefaultInstance();

        cid = getArguments().getInt(EXTRA_CID);
        type = getArguments().getInt(EXTRA_TYPE);

        mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {

            @Override
            public void onGlobalLayout()
            {

                mSwipeRefreshLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSwipeRefreshLayout.setRefreshing(true);
                clearCache();
                getDoubanMeizi();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {

                page = 1;
                clearCache();
                getDoubanMeizi();
            }
        });

        doubanMeizis = realm.where(DoubanMeizi.class)
                .equalTo("type", type)
                .findAll();

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(OnLoadMoreListener(mLayoutManager));
        mAdapter = new DoubanMeiziAdapter(mRecyclerView, doubanMeizis);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void clearCache()
    {

        try
        {
            realm.beginTransaction();
            realm.where(DoubanMeizi.class)
                    .equalTo("type", type)
                    .findAll().clear();
            realm.commitTransaction();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getDoubanMeizi()
    {

        RetrofitHelper.getDoubanMeiziApi()
                .getDoubanMeizi(cid, page)
                .enqueue(new Callback<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {


                        MeiziCacheUtil.getInstance().putDoubanMeiziCache(getActivity(), type, response);
                        finishTask();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {

                        mSwipeRefreshLayout.post(new Runnable()
                        {

                            @Override
                            public void run()
                            {

                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });

                        SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
                    }
                });
    }

    private void finishTask()
    {

        if (page * pageNum - pageNum - 1 > 0)
            mAdapter.notifyItemRangeChanged(page * pageNum - pageNum - 1, pageNum);
        else
            mAdapter.notifyDataSetChanged();
        if (mSwipeRefreshLayout.isRefreshing())
        {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener()
        {

            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder)
            {

                DoubanMeiziPageActivity.luanch(getActivity(), position, type);
            }
        });
    }

    RecyclerView.OnScrollListener OnLoadMoreListener(StaggeredGridLayoutManager layoutManager)
    {

        return new RecyclerView.OnScrollListener()
        {

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy)
            {

                boolean isBottom = mLayoutManager.findLastCompletelyVisibleItemPositions(
                        new int[2])[1] >= mAdapter.getItemCount() - PRELOAD_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom)
                {
                    if (!mIsLoadMore)
                    {
                        mSwipeRefreshLayout.setRefreshing(true);
                        page++;
                        getDoubanMeizi();
                    } else
                    {
                        mIsLoadMore = false;
                    }
                }
            }
        };
    }

    @Override
    public void onDestroy()
    {

        super.onDestroy();
        realm.close();
    }
}
