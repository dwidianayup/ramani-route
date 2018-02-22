package com.ujuizi.ramani.routing.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.routing.navigation.SearchAddress;
import com.ujuizi.ramani.routing.view.adapter.SearchViewAdapter;

import java.util.ArrayList;

/**
 * Created by ujuizi on 1/11/17.
 */

public class SearchAddressView {

    public static int STATUS_START_POINT = 1;
    public static int STATUS_END_POINT = 2;
    public static int STATUS_EMPTY = 0;

    private Activity mActivity;
    private View searchLayout;
    private android.support.v7.widget.SearchView searchViewColumn;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;
    private SearchAddress searchHelper = new SearchAddress();
    private ArrayList<SearchAddressModel> listData = new ArrayList<>();
    private SearchViewAdapter adapter;
    private int status = STATUS_EMPTY;
    private RoutingListener.SearchAddressListener mListener;
    private ImageView btnClose;
    private TextView textNoData;
    private AsyncTask<Void, Void, Void> asyntaskSearch;
    private ImageView searchButton;

    public SearchAddressView(Activity activity, RoutingListener.SearchAddressListener searchListener) {
        mActivity = activity;
        mListener = searchListener;
        initSearchView();
    }

    private void initSearchView() {
        removeView();
        LinearLayout relativeLayout = new LinearLayout(mActivity);

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchLayout = inflater.inflate(R.layout.search_view_layout, relativeLayout);
        searchLayout.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        searchViewColumn = (android.support.v7.widget.SearchView) searchLayout.findViewById(R.id.searchview_column);

        listView = (ListView) searchLayout.findViewById(R.id.listView_search);
        swipeRefresh = (SwipeRefreshLayout) searchLayout.findViewById(R.id.swipe_refresh);
        btnClose = (ImageView) searchLayout.findViewById(R.id.close_button_search);
        textNoData = (TextView) searchLayout.findViewById(R.id.text_no_data_searchaddress);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchLayout();
            }
        });
        // Get the search close button image view
        searchButton = (ImageView)searchViewColumn.findViewById(R.id.search_button);
        ImageView closeButton = (ImageView)searchViewColumn.findViewById(R.id.search_close_btn);

        searchViewColumn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAddress(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAddress(newText);
                return true;
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewColumn.setQuery("",false);
            }
        });

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.addContentView(searchLayout, params);
    }

    private void removeView() {
        if (searchLayout != null) {
            try {
                ViewGroup parent = (ViewGroup) searchLayout.getParent();
                parent.removeView(searchLayout);
                searchLayout = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showSearchLayout(int status) {
        if (searchLayout.getVisibility() == View.GONE) {
//            ViewAnimationUtils.expand(searchLayout);
            searchLayout.setVisibility(View.VISIBLE);
//            ViewAnimationUtils.startAnimation(mActivity,searchLayout);
            searchButton.performClick();
        }
        setStatus(status);
    }

    public void hideSearchLayout() {
        if (searchLayout.getVisibility() == View.VISIBLE) {
//            ViewAnimationUtils.collapse(searchLayout);
            searchLayout.setVisibility(View.GONE);
            status = STATUS_EMPTY;
        }
    }

    public void setStatus(int statusSearch) {
        status = statusSearch;
    }

    private void searchAddress(final String address) {
        if (asyntaskSearch != null) {
            asyntaskSearch.cancel(true);
        }
        asyntaskSearch = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefresh.setRefreshing(true);

                textNoData.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {

                listData.clear();
                listData = searchHelper.searchAddressByName(address);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.e("searchAddress", "size : " + listData.size());
                adapter = new SearchViewAdapter(mActivity, listData, status, mListener);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.invalidateViews();
                swipeRefresh.setRefreshing(false);
                if (listData.size() == 0) {
                    textNoData.setVisibility(View.VISIBLE);
                } else {
                    textNoData.setVisibility(View.GONE);
                }
                if (mListener != null)
                    mListener.onSearchAddressDone(listData);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}
