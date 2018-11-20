package com.example.weioule.inputkeyboarddemo.act;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weioule.inputkeyboarddemo.R;
import com.example.weioule.inputkeyboarddemo.view.CEditText;
import com.example.weioule.inputkeyboarddemo.view.CEditTextSms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * author weioule
 * Created on 2018/3/6.
 */
public class FindPayPwdActivity extends BaseTitleActivity implements View.OnClickListener, CEditTextSms.SendSmsListener {

    private CEditTextSms mVerificationCode;
    private CEditText mEtphone;
    private TextView mSubmit;

    @Override
    protected void initView() {
        addTitleText("找回密码");
        mEtphone = findViewById(R.id.et_phone);
        mVerificationCode = findViewById(R.id.et_verification_code);
        mVerificationCode.setIme(EditorInfo.IME_ACTION_DONE, "");
        mVerificationCode.setSendSmsListener(this);
        mSubmit = findViewById(R.id.submit_btn);
        mSubmit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.find_paypwd_activity;
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(mVerificationCode.getText())) {
            mVerificationCode.showErrNote("验证码为空!");
        } else if (checkSmsCode(mVerificationCode.getText())) {
            Toast.makeText(this, "密码找回成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mVerificationCode.showErrNote("验证码有误!");
        }
    }

    @Override
    public void onSendSms() {
        if (TextUtils.isEmpty(mEtphone.getText())) {
            mEtphone.showErrNote("手机号为空!");
        } else if (isMobileNO(mEtphone.getText())) {
            /**模拟发送验证码成功，开启倒计时*/
            mVerificationCode.onSendSms();
        } else {
            mEtphone.showErrNote("手机号不正确");
        }
    }

    protected void onCheckButtonStatusChanged() {
        String verificationCode = mEtphone.getText().trim();
        if (!TextUtils.isEmpty(verificationCode)) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }

    public static boolean isMobileNO(String mobileNums) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
        // "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 验证验证码
     *
     * @param smsCode
     * @return
     */
    public static boolean checkSmsCode(String smsCode) {
        if (smsCode == null || "".equals(smsCode)) {
            return false;
        }
        Pattern p = Pattern.compile("^([0-9]{6})$");// ^([0-9]{6})$,^\\d{6,8}
        Matcher m = p.matcher(smsCode);
        return m.matches();
    }
}
