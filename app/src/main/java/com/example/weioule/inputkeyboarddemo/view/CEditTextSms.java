package com.example.weioule.inputkeyboarddemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.weioule.inputkeyboarddemo.util.CountDown;
import com.example.weioule.inputkeyboarddemo.R;


/**
 * 发送验证码控件
 * author weioule
 * Created on 2018/3/6.
 */
public class CEditTextSms extends CEditText implements CountDown.OnCountDownListener {
    private TextView smsButton;
    private CountDown countDown;
    private SendSmsListener mSendSmsListener;
    private boolean externalStatus = true; //保存外部状态

    public CEditTextSms(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSmsButton();
        initCountDown();
    }

    private void initSmsButton() {
        smsButton = findViewById(R.id.right_iamge);
        smsButton.setVisibility(View.VISIBLE);
        smsButton.setTextAppearance(getContext(), R.style.font_red_fd6e5f_15);
        smsButton.setGravity(Gravity.CENTER);
        smsButton.setText("发送验证码");
        smsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                sendSms();
            }
        });
    }

    public void sendSms() {
        if (mSendSmsListener != null) {
            mSendSmsListener.onSendSms();
        }
    }

    //短信发送成功
    public void onSendSms() {
        countDown.runTime(CountDown.INTER_S);
    }

    private void initCountDown() {
        countDown = new CountDown(60);
        countDown.setListener(this);
    }

    @Override
    public void onCountDownFinished() {
        setSmsEnable(externalStatus);
    }

    @Override
    public void onCountDownRun(int maxNum, int remainNum) {
        smsButton.setText("" + remainNum + "s后重发");
        smsButton.setTextAppearance(getContext(), R.style.font_gray_9_15);
        smsButton.setEnabled(false);
    }

    public void setSendSmsListener(SendSmsListener mSendSmsListener) {
        this.mSendSmsListener = mSendSmsListener;
    }

    public void setSmsEnable(boolean isEnable) {
        externalStatus = isEnable;
        if (countDown.isRunning()) {
            return;
        }

        if (isEnable) {
            smsButton.setText("发送验证码");
            smsButton.setTextAppearance(getContext(), R.style.font_red_fd6e5f_15);
            smsButton.setEnabled(true);
        } else {
            smsButton.setText("发送验证码");
            smsButton.setTextAppearance(getContext(), R.style.font_red_fd6e5f_15);
            smsButton.setEnabled(false);
        }

    }

    public interface SendSmsListener {
        void onSendSms();
    }
}
