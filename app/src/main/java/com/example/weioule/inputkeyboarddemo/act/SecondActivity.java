package com.example.weioule.inputkeyboarddemo.act;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.weioule.inputkeyboarddemo.keyboard.CumKeyboardContainer;
import com.example.weioule.inputkeyboarddemo.keyboard.InputPayPwdDialog;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class SecondActivity extends MainActivity {

    private InputPayPwdDialog inputPayPwdDialog;

    @Override
    protected String getInputType() {
        addTitleText("结算中心");
        mIndentityCard.setHit("请输入金额");
        mIndentityCard.setLeftText("金额");
        Intent intent = getIntent();
        mIdInfo.setText("性别：" + intent.getStringExtra(ID_INFO_SEX) + "\n" + "出生日期：" + intent.getStringExtra(ID_INFO_BIRTHDAY));
        mIdInfo.setVisibility(View.VISIBLE);
        okBtn.setText("确认支付");
        return CumKeyboardContainer.AMOUT_TYPE;
    }

    @Override
    protected void clickBtn() {
        if (TextUtils.isEmpty(mIndentityCard.getText())) {
            mIndentityCard.showErrNote("金额不能为空！");
            return;
        }
        showInputPaypwdDialog();
        hintKeyBoard();
    }

    private void showInputPaypwdDialog() {
        inputPayPwdDialog = new InputPayPwdDialog(this);
        inputPayPwdDialog.setInputPasswordListener(new InputPayPwdDialog.PasswordListener() {
            @Override
            public void onSubmitPwd(String password) {
                Toast.makeText(SecondActivity.this, "成功支付" + mIndentityCard.getText() + "元", Toast.LENGTH_SHORT).show();
            }
        });
        inputPayPwdDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (hintKeyBoard()) return;
        finish();
    }
}
