package com.fh.bcoin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    /**
     * view 初始化
     */
    public abstract void initView();

    /**
     * 设置监听
     */
    public abstract void initListener();

    /**
     * 初始化数据
     */
    public abstract void initData();
}
