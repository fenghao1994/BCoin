package com.fh.bcoin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.fh.bcoin.R;
import com.fh.bcoin.adapter.TokenHolderAdapter;
import com.fh.bcoin.extra.ExtraType;
import com.fh.bcoin.http.HttpUrl;
import com.fh.bcoin.model.CoinContractsModel;
import com.fh.bcoin.model.TokenHolderModel;
import com.fh.bcoin.model.TokenInfoModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView searchView;
    private ListView addressList;
    private static final int REQUIRE_CODE = 0x1001;
    private List<TokenHolderModel> listTokenHolder;
    private TokenHolderAdapter tokenHolderAdapter;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        searchView = findViewById(R.id.main_search);
        addressList = findViewById(R.id.address_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    @Override
    public void initListener() {
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        addressList.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        listTokenHolder = new ArrayList<>();
        tokenHolderAdapter = new TokenHolderAdapter(this, listTokenHolder);
        addressList.setAdapter(tokenHolderAdapter);
    }

    private void goToSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, REQUIRE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CoinContractsModel contractsModel = null;
        if (requestCode == REQUIRE_CODE && resultCode == RESULT_OK) {
            contractsModel = (CoinContractsModel) data.getSerializableExtra(ExtraType.COIN_CONTRACTS);
        }
        if (contractsModel != null) {
            searchView.setText(contractsModel.getName());
            doSearchTokenInfo(contractsModel);
        }
    }

    private void doSearchTokenInfo(final CoinContractsModel contractsModel) {
        swipeRefreshLayout.setRefreshing(true);
        String url = HttpUrl.QUERY_COIN_INFO + contractsModel.getContracts();
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
                        swipeRefreshLayout.setRefreshing(false);
                        doSearchTokenHolderSearch(contractsModel, parseTokenInfoHtml(response));
                    }
                });
    }

    private void doSearchTokenHolderSearch(CoinContractsModel contractsModel, TokenInfoModel tokenInfoModel) {
        swipeRefreshLayout.setRefreshing(true);
        String total = tokenInfoModel.getTotalSupply();
        for (int i = 0; i < tokenInfoModel.getDecimals(); i++) {
            total += "0";
        }

        Map<String, String> params = new HashMap<>();
        params.put("a", contractsModel.getContracts());
        params.put("s", total);
        OkHttpUtils.get()
                .url(HttpUrl.QUERY_COIN_HOLDER)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseTokenHolderHtml(response);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void showTokenHolder() {
        tokenHolderAdapter.notifyDataSetChanged();
    }

    private void parseTokenHolderHtml(String str) {
        Document document = Jsoup.parse(str);
        Elements tbody = document.select("tbody");
        if (tbody != null && !tbody.isEmpty()) {
            Element element = tbody.get(0);
            Elements tr = element.select("tr");
            dealWithTR(tr);
        }
    }

    private void dealWithTR(Elements tr) {
        if (tr != null && !tr.isEmpty()) {
            int size = tr.size();
            if (size > 20) {
                size = 20;
            }
            for (int i = 0; i < size; i++) {
                Elements th = tr.get(i).select("th");
                Elements td = tr.get(i).select("td");
                TokenHolderModel tokenHolderModel = new TokenHolderModel();
                if (th != null && th.size() == 4) {
                    tokenHolderModel.setRank(th.get(0).text());
                    tokenHolderModel.setAddress(th.get(1).text());
                    tokenHolderModel.setQuantity(th.get(2).text());
                    tokenHolderModel.setPercentage(th.get(3).text());
                }
                if (td.size() == 4) {
                    tokenHolderModel.setRank(td.get(0).text());
                    tokenHolderModel.setAddress(td.get(1).text());
                    tokenHolderModel.setQuantity(td.get(2).text());
                    tokenHolderModel.setPercentage(td.get(3).text());
                }
                listTokenHolder.add(tokenHolderModel);
            }
        }
        showTokenHolder();
    }

    private TokenInfoModel parseTokenInfoHtml(String str) {
        Document doc = Jsoup.parse(str);
        TokenInfoModel tokenInfoModel = new TokenInfoModel();
        Elements table = doc.getElementsByClass("table");
        if (table != null && !table.isEmpty() && table.size() >= 2) {
            setTokenInfoTotal(table.get(0), tokenInfoModel);
            setTokenInfoDecimals(table.get(1), tokenInfoModel);
        }
        return tokenInfoModel;
    }

    private void setTokenInfoDecimals(Element element, TokenInfoModel tokenInfoModel) {
        Elements td = element.select("td");
        int flag = 0;
        for (int i = 0; i < td.size(); i++) {
            if (td.get(i).text().contains("Decimals")) {
                flag = i + 1;
                break;
            }
        }
        Element node = td.get(flag);
        tokenInfoModel.setDecimals(Integer.parseInt(node.text()));
    }

    private void setTokenInfoTotal(Element element, TokenInfoModel tokenInfoModel) {
        String total = element.getElementsByClass("tditem").text();
        String[] split = total.split(" ");
        String numble = "";
        if (split.length >= 2) {
            String[] split1 = split[0].split(",");
            for (String s : split1) {
                numble += s;
            }
            tokenInfoModel.setTotalSupply(numble);
        }
    }
}
