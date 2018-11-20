package com.example.weioule.inputkeyboarddemo.act;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.weioule.inputkeyboarddemo.R;


/**
 * Author by weioule.
 * Date on 2018/10/29.
 */
@SuppressWarnings({"unchecked", "deprecation"})
public abstract class BaseTitleActivity extends AppCompatActivity {

    protected RelativeLayout titleBar;
    private ImageView mRightImage;
    private ImageView mLeftImage;
    private TextView mRightText;
    private TextView mTitleText;
    private TextView mLeftText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_base);

        titleBar = findViewById(R.id.title_layout);
        mTitleText = titleBar.findViewById(R.id.title_tv_title);
        mLeftImage = titleBar.findViewById(R.id.title_iv_left);
        mLeftText = titleBar.findViewById(R.id.title_tv_left);
        mRightImage = titleBar.findViewById(R.id.title_iv_right);
        mRightText = titleBar.findViewById(R.id.title_tv_right);

        mLeftImage.setVisibility(View.VISIBLE);
        mLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int body = this.getLayoutId();
        if (body != 0) {
            LayoutInflater.from(this).inflate(body, (ViewGroup) findViewById(R.id.container), true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initView();
        initData();
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int getLayoutId();

    protected TextView addLeftText(String text) {
        mLeftText.setText(text);
        return mLeftText;
    }

    protected void addLeftImage(@DrawableRes int resid) {
        addLeftImage(getResources().getDrawable(resid));
    }

    protected ImageView addLeftImage(Drawable drawable) {
        mLeftImage.setImageDrawable(drawable);
        return mLeftImage;
    }

    protected void addTitleText(String text) {
        mTitleText.setText(text);
    }

    protected void addRightImage(@DrawableRes int resid) {
        addRightImage(getResources().getDrawable(resid));
    }

    protected ImageView addRightImage(Drawable drawable) {
        mRightImage.setImageDrawable(drawable);
        mRightImage.setVisibility(View.VISIBLE);
        return mLeftImage;
    }

    protected TextView addRightText(String text) {
        mRightText.setText(text);
        return mRightText;
    }

    protected void forwardAndFinish(Class<?> cls) {
        forward(cls);
        finish();
    }

    protected void forward(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    protected int getNavigationBarHeight(Context context) {
        int resourceId;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    /**
     * 判断网络
     */
    public boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
