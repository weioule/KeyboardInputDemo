package com.example.weioule.inputkeyboarddemo.keyboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.weioule.inputkeyboarddemo.act.FindPayPwdActivity;
import com.example.weioule.inputkeyboarddemo.R;


/**
 * author weioule
 * Create on 2018/3/13.
 */
public class InputPayPwdDialog implements View.OnClickListener, KeyboardView.OnKeyboardActionListener {
    private Context context;
    private View inflate;
    private Dialog dialog;
    private int mOldEditLenght;
    private InputView mEditPay;
    private PasswordListener listener;
    private CustomKeyboardView mKeyborView;

    public InputPayPwdDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        inflate = LayoutInflater.from(context).inflate(R.layout.paypwd_input_password_dialog, null);
        mEditPay = inflate.findViewById(R.id.edit_pay);
        mKeyborView = inflate.findViewById(R.id.view_keyboard);
        mKeyborView.setOnKeyboardActionListener(this);
        // 设置软键盘按键的布局
        Keyboard mNumberKeyboard = new Keyboard(context, R.xml.kb_pwd);
        mKeyborView.setKeyboard(mNumberKeyboard);
        inflate.findViewById(R.id.forget_pwd).setOnClickListener(this);
        dialog.setContentView(inflate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.paypwd_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //将属性设置给窗体
        lp.width = d.getWidth();
        dialogWindow.setAttributes(lp);
    }

    public void show() {
        mEditPay.setText("");
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.forget_pwd) {
            context.startActivity(new Intent(context, FindPayPwdActivity.class));
            mKeyborView.post(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }
    }

    public void onInsertKeyEvent(String text) {
        mOldEditLenght = mEditPay.length();
        mEditPay.append(text);
        //输入好密码时回调密码值
        if (mOldEditLenght < 6 && mEditPay.length() == 6) {
            if (null != listener) {
                String password = mEditPay.getText().toString().trim();
                listener.onSubmitPwd(password);
                mKeyborView.post(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();//支付密码错误的时候，直接弹框， 键盘不隐藏
                    }
                });
            }
        }
    }

    public void onDeleteKeyEvent() {
        int start = mEditPay.length();
        if (start > 0) {
            mEditPay.getText().delete(start - 1, start);
        }
    }

    public void setInputPasswordListener(PasswordListener listener) {
        this.listener = listener;
    }

    public String getPassword() {
        return mEditPay.getText().toString().trim();
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            onDeleteKeyEvent();
        }
        // 点击了数字按键
        else if (primaryCode != CustomKeyboardView.KEYCODE_EMPTY) {
            onInsertKeyEvent(Character.toString(
                    (char) primaryCode));
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public interface PasswordListener {
        void onSubmitPwd(String password);
    }
}
