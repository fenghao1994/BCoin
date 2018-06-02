package com.fh.bcoin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fh.bcoin.R;
import com.fh.bcoin.adapter.SearchResultAdapter;
import com.fh.bcoin.extra.ExtraType;
import com.fh.bcoin.http.HttpUrl;
import com.fh.bcoin.model.CoinContractsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class SearchActivity extends BaseActivity {
    private SearchView searchView;
    private ListView listView;
    private SearchResultAdapter searchResultAdapter;
    private List<CoinContractsModel> coinList;
    private String TAG = SearchActivity.class.getSimpleName();
    private Gson gson;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        searchView = findViewById(R.id.search_view);
        listView = findViewById(R.id.search_result_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        searchView.setIconified(false);
    }

    @Override
    public void initListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    coinList.clear();
                    searchResultAdapter.notifyDataSetChanged();
                }
                getSearchResult(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(ExtraType.COIN_CONTRACTS, coinList.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                if (firstVisibleItem == 0){
                    swipeRefreshLayout.setEnabled(true);
                } else{
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void initData() {
        gson = new Gson();
        coinList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(this, coinList);
        listView.setAdapter(searchResultAdapter);
    }

    private void getSearchResult(String text) {
        swipeRefreshLayout.setRefreshing(true);
        String url = HttpUrl.QUERY_CONTRACTS + text;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        List<String> list = gson.fromJson(response, new TypeToken<List<String>>() {
                        }.getType());
                        showSearchResult(parseHtml(list));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void showSearchResult(List<CoinContractsModel> list) {
        searchResultAdapter.notifyDataSetChanged();
    }

    private List<CoinContractsModel> parseHtml(List<String> result) {
        coinList.clear();
        if (result == null || result.size() == 0) {
            return null;
        }
        for (String str : result) {
            CoinContractsModel coinContractsModel = createCoinContractsModel(str);
            if (coinContractsModel != null) {
                coinList.add(coinContractsModel);
            }
        }
        return coinList;
    }

    private CoinContractsModel createCoinContractsModel(String str) {
        int flag = str.indexOf("0x");
        String s1 = str.substring(0, flag);
        String s2 = str.substring(flag, str.length());

        int index = s2.indexOf("\t");
        String contract = s2.substring(0, index);

        CoinContractsModel coinContractsModel = new CoinContractsModel();
        coinContractsModel.setName(s1.replace("\t", ""));
        coinContractsModel.setContracts(contract);
        return coinContractsModel;
    }
}
